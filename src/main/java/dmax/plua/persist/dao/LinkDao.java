package dmax.plua.persist.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import dmax.plua.domain.Language;
import dmax.plua.domain.Link;
import dmax.plua.persist.Dao;

/**
 * Dao class for encapsulate database operations with {@link dmax.plua.domain.Link} instances.
 *
 * <br/><br/>
 * Created by Maxim Dybarsky | maxim.dybarskyy@gmail.com
 * on 15.12.14 at 11:12
 */
class LinkDao extends Dao<Link> {

    public LinkDao() {
        super(Link.class);
    }

    @Override
    public Link insert(SQLiteDatabase db) {
        ContentValues values = prepareContentValues();
        long id = db.insert(TABlE_LINK, null, values);
        persistable.setId(id);
        return persistable;
    }

    @Override
    public Link update(SQLiteDatabase db) {
        ContentValues values = prepareContentValues();
        int count = db.update(TABlE_LINK, values, COLUMN_ID + "=?", new String[]{ String.valueOf(getId()) });
        return count > 0 ? persistable : null;
    }

    @Override
    public Link retrieve(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(SQL_SELECT_BY_ID_LINK, new String[]{ String.valueOf(getId()) });
        if (cursor.getCount() == 0) return null;
        cursor.moveToFirst();

        Link result = createLink(cursor);
        cursor.close();
        return result;
    }

    @Override
    public boolean delete(SQLiteDatabase db) {
        int rows = db.delete(TABlE_LINK, COLUMN_ID + "=" + getId(), null);
        return rows > 0;
    }

    @Override
    public CloseableIterator<Link> retrieveIterator(SQLiteDatabase db) {
        Cursor result = db.rawQuery(SQL_SELECT_ALL_LINKS, null);
        return new LinkIterator(result);
    }

    //~

    private ContentValues prepareContentValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LINK_ORIGINAL, persistable.getWordId(Language.UKRAINIAN));
        values.put(COLUMN_LINK_TRANSLATION, persistable.getWordId(Language.GERMAN));
        values.put(COLUMN_LINK_PRIORITY, persistable.getPriority());
        values.put(COLUMN_LINK_UPDATED, persistable.getUpdated());
        return values;
    }

    private static Link createLink(Cursor cursor) {
        Link link = new Link();
        link.setId(cursor.getLong(COLUMN_ID_INDEX));
        link.setWordId(Language.UKRAINIAN, cursor.getLong(COLUMN_LINK_ORIGINAL_INDEX));
        link.setWordId(Language.GERMAN, cursor.getLong(COLUMN_LINK_TRANSLATION_INDEX));
        link.setPriority(cursor.getInt(COLUMN_LINK_PRIORITY_INDEX));
        link.setUpdated(cursor.getLong(COLUMN_LINK_UPDATED_INDEX));
        return link;
    }

    //~

    private static class LinkIterator implements CloseableIterator<Link> {

        private Cursor cursor;

        private LinkIterator(Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public boolean hasNext() {
            return !cursor.isLast() && cursor.getCount() != 0;
        }

        @Override
        public Link next() {
            cursor.moveToNext();
            return createLink(cursor);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() {
            cursor.close();
        }
    }
}
