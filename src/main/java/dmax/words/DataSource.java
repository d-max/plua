package dmax.words;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dmax.words.domain.Language;
import dmax.words.domain.Link;
import dmax.words.domain.Word;
import dmax.words.persist.Dao;
import dmax.words.persist.DataBaseManager;
import dmax.words.persist.dao.DaoFactory;

/**
 * Main access and operations point with links and words. Uses links cache inside.
 * All operations will be done with database and cache.
 *
 * <br/><br/>
 * Created by Maxim Dybarsky | maxim.dybarskyy@gmail.com
 * on 05.01.15 at 11:21
 */
public class DataSource {

    private Language language;
    private DataBaseManager dataBaseManager;
    private Dao<Word> dao;

    private List<Link> links;

    /**
     * Create data source.
     * @param language default language. Used in methods {@link #loadOriginalWord(dmax.words.domain.Link)}
     * and {@link #loadTranslationWord(dmax.words.domain.Link)}
     */
    public DataSource(DataBaseManager dataBaseManager, Language language) {
        this.dataBaseManager = dataBaseManager;
        this.language = language;
        this.dao = DaoFactory.createDao(Word.class);
    }

    /**
     * Set current default language. Used in methods {@link #loadOriginalWord(dmax.words.domain.Link)}
     * and {@link #loadTranslationWord(dmax.words.domain.Link)}
     */
    public void setSelectedLanguage(Language language) {
        this.language = language;
    }

    /**
     * Get current default language. Used in methods {@link #loadOriginalWord(dmax.words.domain.Link)}
     * and {@link #loadTranslationWord(dmax.words.domain.Link)}
     */
    public Language getSelectedLanguage() {
        return this.language;
    }

    /**
     * Save new words into database. Words should contain different language.
     * Link will be created and saved into database and cache.
     */
    public void addWords(Word word1, Word word2) {
        Link link = new Link();
        Dao<Link> linkDao = DaoFactory.createDao(Link.class);

        link.setWord(dataBaseManager.insert(dao.setPersistable(word1)));
        link.setWord(dataBaseManager.insert(dao.setPersistable(word2)));
        link = dataBaseManager.insert(linkDao.setPersistable(link));

        links.add(link);
    }

    /**
     * Save words into database. This words should be loaded from database before (should contain id).
     */
    public void updateWords(Word word1, Word word2) {
        if (word1.getId() != -1) {
            dataBaseManager.update(dao.setPersistable(word1));
        }
        if (word2.getId() != -1) {
            dataBaseManager.update(dao.setPersistable(word2));
        }
    }

    /**
     * Save link into database. This link should be loaded from database before (should contain id).
     */
    public void updateLink(Link link) {
        Dao<Link> linkDao = DaoFactory.createDao(Link.class);
        dataBaseManager.update(linkDao.setPersistable(link));
    }

    /**
     * Remove link and words from cache and database.
     * @param link link to be removed from cache and database
     * @param word1 word to be removed from database
     * @param word2 word to be removed from database
     */
    public void removeWords(Link link, Word word1, Word word2) {
        links.remove(link);

        Dao<Link> linkDao = DaoFactory.createDao(Link.class);
        Dao<Word> wordDao = DaoFactory.createDao(Word.class);

        dataBaseManager.delete(linkDao.setPersistable(link));
        dataBaseManager.delete(wordDao.setPersistable(word1));
        dataBaseManager.delete(wordDao.setPersistable(word2));
    }

    /**
     * Load word from database which corresponds to id from link and default language passed to constructor.
     * @param link link which contains id of requested word.
     */
    public Word loadOriginalWord(Link link) {
        return loadWord(link, language);
    }

    /**
     * Load word from database which corresponds to id from link and other language then default.
     * @param link link which contains id of requested word.
     */
    public Word loadTranslationWord(Link link) {
        return loadWord(link, Language.UKRAINIAN.equals(language)
                                ? Language.POLISH
                                : Language.UKRAINIAN);
    }

    private Word loadWord(Link link, Language language) {
        Word target = new Word();
        target.setId(link.getWordId(language));
        target.setLanguage(language);
        return dataBaseManager.retrieve(dao.setPersistable(target));
    }

    /**
     * Return cached collection of links or load if cache not exists.
     * Collection is sorted by priority and updated time.
     */
    public List<Link> getLinks() {
        if (links == null) loadLinks();
        return links;
    }

    /**
     * Load links from database and cache them. Sort by priority.
     */
    private void loadLinks() {
        links = new LinkedList<Link>();
        Dao<Link> linkDao = DaoFactory.createDao(Link.class);
        Iterator<Link> it = dataBaseManager.retrieveIterator(linkDao);

        while (it.hasNext()) links.add(it.next());

        Collections.sort(links, new PrioritySorter());
    }

    /**
     * Clear cached links collection. Will be loaded again when call {@link #getLinks()}.
     */
    public void reset() {
        links = null;
    }

    //~

    /**
     * Used for collection sorting by priority and updated time.
     * After sorting at beginning of collection should be links with big priority value.
     * If priorities are same, link with less updated time should be before link with bigger time.
     * That means, link which was updated earlier (but has same priority) should be shown first.
     */
    static class PrioritySorter implements Comparator<Link> {

        @Override
        public int compare(Link lhs, Link rhs) {
            /*
            if priorities different: return <0 if right link priority less, >0 if lest priority less.
            if priorities same: return <0 if left updated time less, >0 if right updated time less.
            */
            int priorityDiff = rhs.getPriority() - lhs.getPriority();
            return priorityDiff != 0
                    ? priorityDiff
                    : (int) (lhs.getUpdated() - rhs.getUpdated());
        }
    }
}
