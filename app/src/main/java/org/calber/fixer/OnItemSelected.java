package org.calber.fixer;

/**
 * Created by calber on 16/3/16.
 */
public interface OnItemSelected {
    void onDataReady(Object object, int position);
    void onDataSelection(Object object, int position);
    void onDataRemoved(Object object, int position);
}
