package org.projects.shoppinglist;

import android.content.Context;
import android.os.Bundle;
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

    ArrayAdapter<Product> adapter;
    ListView listView;
    ArrayList<Product> bag = new ArrayList<Product>();
    static MyDialogFragment dialog;
    Spinner quantitySpinner;


    public ArrayAdapter getMyAdapter()
    {
        return adapter;
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

        bag.add(new Product("æble", 2));
        bag.add(new Product("Chokolade", 2));

        if (savedInstanceState!=null) {
            //ArrayList<Product> savedProducts = savedInstanceState.getParcelableArrayList("saveBag");
            //his.bag = savedProducts;
         }

        listView = (ListView) findViewById(R.id.list);
        adapter =  new ArrayAdapter<Product>(this,android.R.layout.simple_list_item_checked,bag );

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addToBag();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getMyAdapter().notifyDataSetChanged();
            }
        });

        Button deleteSelectedItems = (Button) findViewById(R.id.deleteSelectedItems);
        deleteSelectedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedItems();
                getMyAdapter().notifyDataSetChanged();
            }
        });
    }

    public void addToBag() {

        EditText productText = (EditText)findViewById(R.id.productText);
        String quantityText = (String)quantitySpinner.getSelectedItem();
        Product p1 = new Product(productText.getText() + "", Integer.parseInt( quantityText+ ""));

        if(!TextUtils.isEmpty(productText.getText()) && !TextUtils.isEmpty(quantityText)){
            bag.add(p1);
            createToast("Dit produkt blev oprettet");

        }else{
            createToast("Dine produkt felter er tomme");
        }

        ArrayList<EditText> list = new ArrayList<>();
        list.add(productText);
        clearTextFields(list);
    }



    public void deleteSelectedItems(){
        SparseBooleanArray checkedItemsBoolan = listView.getCheckedItemPositions();
        ArrayList<Integer> cheked = new ArrayList<Integer>();

        for(int i = 0; i < checkedItemsBoolan.size(); i++){
            if(checkedItemsBoolan.valueAt(i)){
                int position = checkedItemsBoolan.keyAt(i);
                cheked.add(position);
            }
        }
        //TO Do tjek op på bug
        //når man har slettet 2 også sletter igen så bugger den
        deleteItem(cheked);

    }

    public void deleteItem(ArrayList<Integer> indexofDeletetItem){
        for(Integer i : indexofDeletetItem){
            Product item = bag.get(i);
            bag.remove(item);
            System.out.println("Item: " + item + " ID: " + i);
        }
    }

    public void clearEntireList(View view){
        dialog = new MyDialog();
        dialog.show(getFragmentManager(), "MyFragment");

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
        bag.clear();
        getMyAdapter().notifyDataSetChanged();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
