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

public class MainActivity extends AppCompatActivity {


    ArrayAdapter<String> adapter;
    ListView listView;
    ArrayList<String> bag = new ArrayList<String>();

    public ArrayAdapter getMyAdapter()
    {
        return adapter;
    }

    //This method is called before our activity is destoryed
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //ALWAYS CALL THE SUPER METHOD - To be nice!
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("saveBag", bag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bag.add("Æble 2");
        bag.add("Banan 4");
        bag.add("Slappy John 5");
        bag.add("Chokolade 6");

        if (savedInstanceState!=null) {
            this.bag = savedInstanceState.getStringArrayList("saveBag");
        }

        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        listView = (ListView) findViewById(R.id.list);
        //here we create a new adapter linking the bag and the
        //listview
        adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked,bag );

        //setting the adapter on the listview
        listView.setAdapter(adapter);
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
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
                //The next line is needed in order to say to the ListView
                //that the data has changed - we have added stuff now!
                getMyAdapter().notifyDataSetChanged();
            }
        });

        Button deleteSelectedItems = (Button) findViewById(R.id.deleteSelectedItems);
        deleteSelectedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedItems();
                //The next line is needed in order to say to the ListView
                //that the data has changed - we have added stuff now!
                getMyAdapter().notifyDataSetChanged();
            }
        });


    }

    public void addToBag() throws Exception {


        URL url = new URL("http://192.168.87.110/api/AnZIbmJ2dDdG4PkKfwHzs2nW42IRofJt3mmTCaA4/groups/4/action");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();

        con.setRequestMethod("PUT");

        String body = "{\"on\":true, \"sat\":254, \"bri\":254,\"hue\":42632}";


        InputStream is = new ByteArrayInputStream( body.getBytes("UTF-8"));


        con.setDoOutput(true);

         con.getOutputStream();


        DataOutputStream wr = new DataOutputStream (
                con.getOutputStream());
        wr.writeBytes(body);
        wr.close();


        con.disconnect();
        /*EditText productText = (EditText)findViewById(R.id.productText);
        EditText quantityText = (EditText)findViewById(R.id.quantityText);

        String toBag = productText.getText() + " " + quantityText.getText();

        if(!TextUtils.isEmpty(productText.getText()) && !TextUtils.isEmpty(quantityText.getText())){
            bag.add(toBag);
            createToast("Dit produkt blev oprettet");

        }else{
            createToast("Dine produkt felter er tomme");
        }

        ArrayList<EditText> list = new ArrayList<>();
        list.add(productText);
        list.add(quantityText);

        clearTextFields(list);*/
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
            String item = bag.get(i);
            bag.remove(item);

            System.out.println("Item: " + item + " ID: " + i);
        }
      //  adapter.notifyDataSetChanged();
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
