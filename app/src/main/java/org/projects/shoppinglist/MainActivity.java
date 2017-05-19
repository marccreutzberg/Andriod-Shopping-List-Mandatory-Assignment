package org.projects.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.SparseBooleanArray;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
/**
 * Created by Marc Creutzberg.
 */

public class MainActivity extends AppCompatActivity implements MyDialogFragment.OnPositiveListener {
    //Database Referense
    DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("items");

    //Shopping list View'et
    ListView listView;

    //The delete all dialog
    static MyDialogFragment dialog;

    //Quantity spinner
    Spinner quantitySpinner;

    //The fireBaseAdapter
    FirebaseListAdapter<Product> fbListAdapter;

    static Context context;


    //When the screen turns
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        EditText productText = (EditText)findViewById(R.id.productText);
        outState.putString("inputValue", productText.getText() + "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setting theme
        String theme = MyPreferenceFragment.getSettingsThemekey(this);
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(theme, "style", packageName);
        super.setTheme(resId);
        super.onCreate(savedInstanceState);

        this.context = this;
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Getting the data from savedInstanceState if not null
        if (savedInstanceState!=null) {
            String product = savedInstanceState.getString("inputValue");
            EditText productText = (EditText)findViewById(R.id.productText);
            productText.setText(product);
        }

        //Creating the ListView with firebaseListAdapter
        listView = (ListView) findViewById(R.id.list);
        fbListAdapter = new FirebaseListAdapter<Product>(this, Product.class, android.R.layout.simple_list_item_checked, firebase) {
            @Override
            protected void populateView(View view, Product product, int position) {
                TextView tw = ((TextView)view.findViewById(android.R.id.text1));
                tw.setText(product.toString());
            }
        };
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(fbListAdapter);

        //Creating the Add Button
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addToBag();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getFirebaseListAdapter().notifyDataSetChanged();
            }
        });

        //Creating the DeleteSelecedItems Button
        Button deleteSelectedItems = (Button) findViewById(R.id.deleteSelectedItems);
        deleteSelectedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedItems();
                getFirebaseListAdapter().notifyDataSetChanged();
            }
        });

        //SPINNER
        quantitySpinner = (Spinner) findViewById(R.id.quantitySpinner);
        ArrayAdapter<CharSequence> quantitySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, android.R.layout.simple_spinner_item);
        quantitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantitySpinner.setAdapter(quantitySpinnerAdapter);

        //Name Text
        String name = MyPreferenceFragment.getName(this);
        TextView userName = (TextView) findViewById(R.id.userName);
        if(userName.toString() != "" ){
            userName.setText(name);
        }else{
            userName.setText("Welcome to Shopping list, you can enter your name the settings");
        }
    }

    /**
     * Adds a new item to the databse
     */
    public void addToBag() {
        EditText productText = (EditText)findViewById(R.id.productText);
        String quantityText = (String)quantitySpinner.getSelectedItem();

        if(!TextUtils.isEmpty(productText.getText()) && !TextUtils.isEmpty(quantityText)){
            Product p1 = new Product(productText.getText() + "", quantityText+ "");
            firebase.push().setValue(p1);
            getFirebaseListAdapter().notifyDataSetChanged();

            createToast("Your product was created");

        }else{
            createToast("Your product fields are empty");
        }

        ArrayList<EditText> list = new ArrayList<>();
        list.add(productText);
        clearTextFields(list);
    }

    /**
     * deleteSelecedItems
     *
     * Deleing the selected items from the listView
     */
    public void deleteSelectedItems(){
        //checkedItemsBoolan giving a array of all items i listview
        //If the item is checked the value is True if not the value if False
        SparseBooleanArray checkedItemsBoolan = listView.getCheckedItemPositions();

        //list of all selected items in listView
        ArrayList<Integer> cheked = new ArrayList<Integer>();

        //Last Deleted product for snackbar
        final ArrayList<Product> lastDeletedProducts = new ArrayList<>();

        for(int i = 0; i < checkedItemsBoolan.size(); i++){
            //if the item is selected in listView
            if(checkedItemsBoolan.valueAt(i)){
                int position = checkedItemsBoolan.keyAt(i);
                //adding the item to "checked" array
                cheked.add(position);
            }
        }

        for(Integer i : cheked){
            //adding objects to lastDeletedProducts for snackBar
            Product p = getItem(i);
            lastDeletedProducts.add(p);

            //deleting items in firebase
            getFirebaseListAdapter().getRef(i).setValue(null);
        }

        final View parent = listView;
        Snackbar snackbar = Snackbar
                .make(parent, "Item Deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        for(Product p: lastDeletedProducts){
                            firebase.push().setValue(p);
                        }

                        getFirebaseListAdapter().notifyDataSetChanged();
                        Snackbar snackbar = Snackbar.make(parent, "Item restored!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
        snackbar.show();

        //clearing the checked list
        checkedItemsBoolan.clear();
        cheked.clear();
    }

    /**
     * Share list
     *
     * looping through all items i firebase and adds name and quantity to "textTOShare" string
     * then creating a Intent that send a text
     *
     */
    public void shareList(){
        String textToShare = "- - -Shopping list Items- - - \n  \n";

        for(int i = 0; i < getFirebaseListAdapter().getCount(); i++){
            int counter = i + 1;

            textToShare += counter + ": " + getItem(i) + "\n";
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    /**
     * clearEntireList
     *
     * Creating the dialog for clearing the Entire List
     */
    public void clearEntireList(){
        dialog = new MyDialog();
        dialog.show(getFragmentManager(), "MyFragment");

        //calling onPositiveClicked in dialog
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * onOptionsItemSelected
     *
     * Menu item Press method
     *
     * Making a switch case if for see which menu items if pressed
     *
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                Toast.makeText(this, "Application icon clicked!",
                        Toast.LENGTH_SHORT).show();
                return true; //return true, means we have handled the event
            case R.id.item_clearAll:
                clearEntireList();
                return true;
            case R.id.item_shareList:
                shareList();
                return true;
            case R.id.item_settings:
                settings();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * settings
     * Creating the setting Intent
     * @return
     */
    public boolean settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 1);
        return true;
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) //the code means we came back from settings
        {
            finish();
            startActivity(getIntent());
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public static class MyDialog extends MyDialogFragment {
        @Override
        protected void negativeClick() {
            //Here we override the method and can now do something
            Toast toast = Toast.makeText(context,
                    "The list is as normal", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * onPositiveClicked
     *
     * If pressed yes in the dialog then deleting the entire list.
     */
    @Override
    public void onPositiveClicked() {
        createToast("The list is now empty");
        firebase.setValue(null);
    }


    /**
     * Helper method to clear all input fields
     * @param editTextArraylist
     */
    public void clearTextFields(ArrayList<EditText> editTextArraylist){
        for(EditText v: editTextArraylist){
            v.setText("");
        }
    }

    /**
     * Helper method to creaing a Toast
     * @param msg
     */
    public void createToast(String msg){
        //Creating a Toast
        Toast toast;
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        CharSequence text = "";
        if(msg.length() > 0){
            text = msg;
        }else{
            text = "Error";
        }

        //Fylder data i min toast
        toast = Toast.makeText(context, text, duration);

        //Placere toastern
        toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);

        //Viser Toasten
        toast.show();
    }

    /**
     * Helper Method to getItem from a intex in the FirebaseListAdapter
     * @param index
     * @return
     */
    public Product getItem(int index) {
        return (Product) getFirebaseListAdapter().getItem(index);
    }

    /**
     * Getter for FirebaseListAdapter
     * @return
     */
    public FirebaseListAdapter getFirebaseListAdapter() {
        return fbListAdapter;
    }
}