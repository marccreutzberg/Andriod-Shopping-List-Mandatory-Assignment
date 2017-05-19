package org.projects.shoppinglist;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Marc Creutzberg
 */

public class SettingsActivity  extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MyPreferenceFragment())
                .commit();
    }
}
