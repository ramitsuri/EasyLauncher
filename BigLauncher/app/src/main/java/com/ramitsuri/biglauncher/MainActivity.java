package com.ramitsuri.biglauncher;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private LinkedList<String> appListItems;
    ListView appListView;
    ApplicationAdapter appAdapter = null;
    ProgressDialog progressDialog = null;
    Runnable viewApps = null;
    ArrayList<PackageInfo> packageList = null;
    Applications myApps = null;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    SearchView search_view;
    JSONObject jsonObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setVisibility(View.INVISIBLE);
        ActionBar ab = getSupportActionBar();
        ab.hide();
        search_view = (SearchView) findViewById(R.id.search_view);

        packageList = new ArrayList();
        appAdapter = new ApplicationAdapter(this, R.layout.list_layout, packageList);

        appListView = (ListView)findViewById(R.id.appListView);
        appListView.setAdapter(appAdapter);

        viewApps = new Runnable(){
            public void run(){
                getApps();
            }
        };

        Thread appLoaderThread = new Thread(null, viewApps,
                "AppLoaderThread");
        appLoaderThread.start();

        progressDialog = ProgressDialog.show(this,
                "Hold on...", "Loading your apps...", true);

        search_view.setOnQueryTextListener(this);

        //appAdapter.getFilter().filter("S");
//        String [] mStrings = {"Hello","World","this","is","your","end"};
//        appListItems = new LinkedList<>();
//        appListItems.addAll(Arrays.asList(mStrings));
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, appListItems);

        appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                AppInfo appInfo = appAdapter.getItem(position);

                Toast.makeText(getBaseContext(),appInfo.appName + " " ,Toast.LENGTH_SHORT).show();
                PackageManager pm = getPackageManager();
                Intent startActivityIntent = pm.getLaunchIntentForPackage(appInfo.packageName);
                startActivity(startActivityIntent);

            }
        });

    }

    private JSONObject getTagsForPackages(ArrayList<AppInfo> plist) {

        String packageIDs[] = new String[plist.size()];
        int i=0;
        for(AppInfo appInfo: plist){
            packageIDs[i] = appInfo.packageName;
            i++;
        }
        //String packageIDs[] = {"com.amazon.mShop.android.shopping","com.google.android.youtube","com.gombosdev.displaytester","com.oasisfeng.greenify","com.mcdonalds.app","com.afwsamples.testdpc","com.google.android.googlequicksearchbox","org.telegram.messenger","com.google.android.apps.docs.editors.docs","com.mediocre.commute"};
        HttpURLConnection urlConnection = null;

        String url = "http://10.107.193.113:8080/apps/details";
        try {
            // create connection
            URL urlToRequest = new URL(url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            JSONObject ids = new JSONObject();
            ids.put("ids", new JSONArray(packageIDs));
            /*clientData.put("clientID", androidID);*/
            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream ());
            out.write(ids.toString().getBytes("UTF-8"));
            out.flush();
            out.close();
            StringBuilder sb = new StringBuilder();
            int HttpResult =urlConnection.getResponseCode();
            if(HttpResult ==HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                jsonObj = new JSONObject(sb.toString());
                br.close();

                System.out.println(""+sb.toString());

            }else{
                String a = urlConnection.getResponseMessage();
                System.out.println(urlConnection.getResponseMessage());

            }
        } catch (MalformedURLException e) {
            // handle invalid URL
        } catch (SocketTimeoutException e) {
            // hadle timeout
        } catch (IOException e) {
            String a = "ds";
            // handle I/0
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

return jsonObj;
    }

    private JSONObject getTagsForOnePackage(AppInfo appInfo) {

        //String packageIDs[] = {"com.amazon.mShop.android.shopping","com.google.android.youtube","com.gombosdev.displaytester","com.oasisfeng.greenify","com.mcdonalds.app","com.afwsamples.testdpc","com.google.android.googlequicksearchbox","org.telegram.messenger","com.google.android.apps.docs.editors.docs","com.mediocre.commute"};
        HttpURLConnection urlConnection = null;

        String url = "http://10.107.193.113:8080/app/details";
        try {
            // create connection
            URL urlToRequest = new URL(url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            JSONObject ids = new JSONObject();
            ids.put("id", appInfo.packageName);
            /*clientData.put("clientID", androidID);*/
            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream ());
            out.write(ids.toString().getBytes("UTF-8"));
            out.flush();
            out.close();
            StringBuilder sb = new StringBuilder();
            int HttpResult =urlConnection.getResponseCode();
            if(HttpResult ==HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                jsonObj = new JSONObject(sb.toString());
                br.close();

                System.out.println(""+sb.toString());

            }else{
                String a = urlConnection.getResponseMessage();
                System.out.println(urlConnection.getResponseMessage());

            }
        } catch (MalformedURLException e) {
            // handle invalid URL
        } catch (SocketTimeoutException e) {
            // hadle timeout
        } catch (IOException e) {
            String a = "ds";
            // handle I/0
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return jsonObj;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        appAdapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                //handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    protected void handleMenuSearch(){
        ActionBar action = getSupportActionBar(); //get the actionbar

        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_search));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText)findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
//            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                        doSearch();
//                        Toast.makeText(getApplicationContext(), edtSeach.getText(), Toast.LENGTH_SHORT);
//                        return true;
//                    }
//                    return false;
//                }
//            });
            edtSeach.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Toast.makeText(getApplicationContext(), edtSeach.getText(), Toast.LENGTH_SHORT);
                    String text = edtSeach.getText().toString().toLowerCase(Locale.getDefault());
                    appAdapter.getFilter().filter(text);
                }

                @Override
                public void afterTextChanged(Editable s) {

                    //Toast.makeText(getApplicationContext(), text + " after", Toast.LENGTH_SHORT);

                }
            });


            //edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));

            isSearchOpened = true;
        }
    }

    private void doSearch() {

    }



    public class Applications{
        private ArrayList<AppInfo> packageList = null;
        private List activityList = null;
        private Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        private PackageManager packMan = null;

        public Applications(PackageManager packManager){
            packMan = packManager;
            packageList = this.createPackageList(false);
            activityList = this.createActivityList();
            this.addClassNamesToPackageList();
        }

        public ArrayList getPackageList(){
            return packageList;
        }

        public List getActivityList(){
            return activityList;
        }

        private ArrayList<AppInfo> createPackageList(boolean getSysPackages){
            ArrayList<AppInfo> pList = new ArrayList();
            try {
            List packs = getPackageManager().getInstalledPackages(0);
            int count = 0;
            String [] ids = new String[10];
            for(int i = 0; i < packs.size(); i++){
                PackageInfo packInfo = (PackageInfo)packs.get(i);

                if ((!getSysPackages) && (packInfo.versionName == null)){
                    continue ;
                }

                AppInfo newInfo = new AppInfo();

                newInfo.appName = packInfo.applicationInfo.loadLabel(
                        getPackageManager()).toString();
                newInfo.packageName = packInfo.packageName;
                newInfo.versionName = packInfo.versionName;
                newInfo.versionCode = packInfo.versionCode;
                newInfo.icon = (Drawable)packInfo.applicationInfo.loadIcon(
                        getPackageManager());

                if(getApplicationContext().getPackageManager().getLaunchIntentForPackage(packInfo.packageName) != null){
                    //If you're here, then this is a launch-able app
                    //if(newInfo.appName.startsWith("S"))new String[0]

                    if(count<=10) {
                        count++;
                        JSONObject tagsJson = getTagsForOnePackage(newInfo);
                        JSONArray tags = tagsJson.getJSONArray("tags");

                        //ArrayList<String> list = new ArrayList<String>();
                        for (int j = 0; j < tags.length(); j++) {
                            newInfo.tags+=tags.getString(j);
                        }
                        //newInfo.tags = list.toArray(new String[0]);
                        pList.add(newInfo);
                    }
                    else
                        break;
                }
               //Collections.sort(pList, new ResolveInfo.DisplayNameComparator(packMan));

            }

            //pList = new ArrayList(pList.subList(0,10));
            JSONObject jsonObject = getTagsForPackages(pList);

            //getTagsForPackages(pList);
            /*for(String app:jsonObject.getString("ids")){

            }*/

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return pList;
        }

        private List createActivityList(){
            List<ResolveInfo> aList = packMan.queryIntentActivities(mainIntent, 0);

            Collections.sort(aList,
                    new ResolveInfo.DisplayNameComparator(packMan));

            return aList;
        }

        private void packageDebug(){
            if(null == packageList){
                return;
            }

            for(int i = 0; i < packageList.size(); ++i){
                Log.v("PACKINFO: ", "\t" +
                        ((AppInfo) packageList.get(i)).appName + "\t" +
                        ((AppInfo) packageList.get(i)).packageName + "\t" +
                        ((AppInfo) packageList.get(i)).className + "\t" +
                        ((AppInfo) packageList.get(i)).versionName + "\t" +
                        ((AppInfo) packageList.get(i)).versionCode);
            }
        }

        private void activityDebug(){
            if(null == activityList){
                return;
            }

            for(int i = 0; i < activityList.size(); i++){
                ActivityInfo currentActivity = ((ResolveInfo) activityList.get(
                        i)).activityInfo;
//                Log.v("ACTINFO",
//                        "pName="
//                                + currentActivity.applicationInfo.packageName +
//                                " cName=" + currentActivity.name);
            }
        }

        private void addClassNamesToPackageList(){
            if(null == activityList || null == packageList){
                return;
            }

            String tempName = "";

            for(int i = 0; i < packageList.size(); ++i){
                tempName = ((AppInfo) packageList.get(i)).packageName;

                for(int j = 0; j < activityList.size(); ++j){
                    if(tempName.equals(((ResolveInfo) activityList.get(
                            j)).activityInfo.applicationInfo.packageName)){
                        ((AppInfo) packageList.get(i)).className = ((ResolveInfo) activityList.get(
                                j)).activityInfo.name;
                    }
                }
            }
        }
    }

    public class AppInfo{
        private String appName = "";
        private String packageName = "";
        private String className = "";
        private String versionName = "";
        private Integer versionCode = 0;
        private String tags = "";
        private Drawable icon = null;

        public String getAppName(){
            return appName;
        }

        public String getPackageName(){
            return packageName;
        }

        public String getClassName(){
            return className;
        }

        public String getVersionName(){
            return versionName;
        }

        public Integer getVersionCode(){
            return versionCode;
        }

        public Drawable getIcon(){
            return icon;
        }

        public String getTags(){
            return tags;
        }
    }

    public class ApplicationAdapter extends ArrayAdapter implements Filterable{
        private ArrayList items;

        public ApplicationAdapter(Context context, int textViewResourceId,
                                  ArrayList items){
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent){
            View view = convertView;

            if(view == null){
                LayoutInflater layout = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                view = layout.inflate(R.layout.list_layout, null);
            }

                AppInfo appInfo = (AppInfo) items.get(position);
                if (appInfo != null) {
                    TextView appName = (TextView) view.findViewById(
                            R.id.appLauncherRowAppName);
                    ImageView appIcon = (ImageView) view.findViewById(
                            R.id.appIcon);

                    if (appName != null) {
                        appName.setText(appInfo.getAppName());
                    }
                    if (appIcon != null) {
                        appIcon.setImageDrawable(appInfo.getIcon());
                    }
                }

            return view;
        }


        public Filter getFilter(){
            Filter filter = new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    final List tempFliteredDataList = new ArrayList();
                    // We implement here the filter logic
                    if (constraint == null || constraint.toString().trim().length() == 0) {
                        // No filter implemented we return all the list
                        results.values = items;
                    } else {
                        // We perform filtering operation
                        String constrainString = constraint.toString().toLowerCase();
                        for (Object appInfo: items) {

                            if (((AppInfo) appInfo).tags.toLowerCase().contains(constrainString)) {
                                tempFliteredDataList.add(appInfo);
                            }
                        }
                        results.values = tempFliteredDataList ;
                    }
                    return results;

                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.values!=null){
                        items = (ArrayList) results.values; // returns the filtered list based on the search
                        notifyDataSetChanged();
                    }
                    else
                        notifyDataSetInvalidated();
                }
            };
            return filter;
        }
        @Override
        public AppInfo getItem(int position){
            return (AppInfo)items.get(position);
        }

        @Override
        public int getCount(){
            return items != null? items.size() : 0;
        }

    }

    private void getApps(){
        try{
            myApps = new Applications(getPackageManager());
            packageList = myApps.getPackageList();
        }
        catch(Exception exception){
            Log.e("BACKGROUND PROC:", exception.getMessage());
        }
        this.runOnUiThread(returnRes);
    }

    private Runnable returnRes = new Runnable(){
        public void run(){
            if(packageList != null && packageList.size() > 0){
                appAdapter.notifyDataSetChanged();

                for(int i = 0; i < packageList.size(); ++i){
                    appAdapter.add(packageList.get(i));
                }
            }
            progressDialog.dismiss();
            appAdapter.notifyDataSetChanged();
        }
    };

}
