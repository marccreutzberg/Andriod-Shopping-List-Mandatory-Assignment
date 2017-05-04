package org.projects.shoppinglist;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyDialogFragment.OnPositiveListener {

    //TO DO
    // Appen Crasher efter Parcelable på adapter på savedInstanceState i OnCreate
    //
    //når man har slettet 2 også sletter igen så bugger den

    //I top navigation -- aways virker ikke ?  android:showAsAction="always"

    //GAMLE ADAPTER
   // ArrayAdapter<Product> adapter;

    DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("items");
    ListView listView;
    ArrayList<Product> bag = new ArrayList<Product>();
    static MyDialogFragment dialog;
    Spinner quantitySpinner;
    FirebaseListAdapter<Product> fbListAdapter;


    /* GAMLE ADAPTER
    public ArrayAdapter getMyAdapter()
    {
        return adapter;
    }*/

    public FirebaseListAdapter getFirebaseListAdapter(){
        return fbListAdapter;
    }


    static Context context;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("savedBag", bag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //SPINNER
        quantitySpinner = (Spinner) findViewById(R.id.quantitySpinner);
        ArrayAdapter<CharSequence> quantitySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, android.R.layout.simple_spinner_item);
        quantitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantitySpinner.setAdapter(quantitySpinnerAdapter);


        if (savedInstanceState!=null) {
            //ArrayList<Product> savedProducts = savedInstanceState.getParcelableArrayList("saveBag");
            //his.bag = savedProducts;
        }

        //OLD LIST VIEW
       /* listView = (ListView) findViewById(R.id.list);
        adapter =  new ArrayAdapter<Product>(this,android.R.layout.simple_list_item_checked,bag );
        listView.setAdapter(adapter);
        */


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
        Button deleteSelectedItems = (Button) findViewById(R.id.deleteSelectedItems);
        deleteSelectedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedItems();
                getFirebaseListAdapter().notifyDataSetChanged();
            }
        });
    }

    public void addToBag() {
        EditText productText = (EditText)findViewById(R.id.productText);
        String quantityText = (String)quantitySpinner.getSelectedItem();

        if(!TextUtils.isEmpty(productText.getText()) && !TextUtils.isEmpty(quantityText)){
            Product p1 = new Product(productText.getText() + "", Integer.parseInt( quantityText+ ""));
            firebase.push().setValue(p1);
            getFirebaseListAdapter().notifyDataSetChanged();

            createToast("Dit produkt blev oprettet");

        }else{
            createToast("Dine produkt felter er tomme");
        }

        ArrayList<EditText> list = new ArrayList<>();
        list.add(productText);
        clearTextFields(list);
    }



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

    public void clearEntireList(){
        dialog = new MyDialog();
        dialog.show(getFragmentManager(), "MyFragment");

        //calling onPositiveClicked in dialog
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                Toast.makeText(this, "Application icon clicked!",
                        Toast.LENGTH_SHORT).show();
                return true; //return true, means we have handled the event
            case R.id.item_clearAll:
                clearEntireList();

                return true;
        }

        return false; //we did not handle the event
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

    @Override
    public void onPositiveClicked() {
        createToast("The list is now empty");
        firebase.setValue(null);
    }


    public void clearTextFields(ArrayList<EditText> editTextArraylist){
        for(EditText v: editTextArraylist){
            v.setText("");
        }
    }

    public void createToast(String msg){

        //Laver en Toast
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public Product getItem(int index)
    {
        return (Product) getFirebaseListAdapter().getItem(index);

    }
}
