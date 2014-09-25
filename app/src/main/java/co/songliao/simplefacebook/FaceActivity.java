package co.songliao.simplefacebook;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;


public class FaceActivity extends ActionBarActivity  {

    private WebView webview;
    private Menu optionsMenu;
    private ImageButton showMenuButton;
    private static final String Facebook_URL = "http://www.facebook.com";


    public FaceActivity() {
        super();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);


        showMenuButton = (ImageButton)findViewById(R.id.imageButton);

        if(!isConnectingToInternet()){

            Toast.makeText(getApplicationContext(), "Not connected to Internet",
                    Toast.LENGTH_SHORT).show();

        }

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.setWebViewClient(new CustomWebViewClient());
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAllowFileAccessFromFileURLs(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.capturePicture();

        //webview.getAccessibilityNodeProvider().performAction().

        checkMenu();
        webview.loadUrl(Facebook_URL);

    }

    private boolean ActionBarVisible = true;
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {

            webview.goBack(); // go back in only the web view
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showMenuClick(View view){


        final ActionBar actionbar = getActionBar();
        actionbar.show();
        ActionBarVisible=true;
        checkMenu();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void checkMenu(){

        if(ActionBarVisible){
            //if the action bar is shown, then hide the other button
            showMenuButton.setEnabled(false);
            showMenuButton.setVisibility(View.INVISIBLE);
        }
        else {
            showMenuButton.setEnabled(true);
            showMenuButton.setVisibility(View.VISIBLE);
        }
    }




    class CustomWebViewClient extends WebViewClient
    {

        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            //checkMenu();
            setRefreshActionButtonState(true);

        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            //checkMenu();
            setRefreshActionButtonState(false);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            if (errorCode == ERROR_TIMEOUT) {
                view.stopLoading();  // may not be needed
                //view.loadData(timeoutMessageHtml, "text/html", "utf-8");
            }
        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.face, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.airport_menuRefresh:
                webview.loadUrl(Facebook_URL);
                // Complete with your code
                setRefreshActionButtonState(true);
                return true;
            case R.id.action_fullScreen://if enter full screen
                final ActionBar actionbar = getActionBar();

                actionbar.hide();//hide the menu

                ActionBarVisible=false;//set the boolean to false
                checkMenu();
                ///menuIsVisible=false;
                return true;
            case R.id.about:

                final AlertDialog alert = new AlertDialog.Builder(FaceActivity.this).create();
                alert.setTitle("Disclaimer:");
                alert.setMessage("This is not the official facebook app.This app is made purely for educational purpose. No monetary incentive. No copyright infringement intended. @Song Liao,2014");
                alert.setButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alert.cancel();
                    }
                });
                alert.show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setRefreshActionButtonState(final boolean refreshing) {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu.findItem(R.id.airport_menuRefresh);
            if (refreshItem != null) {
                if (refreshing) {

                    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }


    @Override
    public void onBackPressed()
    {

        if (webview.canGoBack())
        {
            webview.goBack();

        }
        else
        {
            //super.onBackPressed();
            super.onStop();//need to fix this later
        }


    }
    //if internet is not availalbe, do not throw an exception, throw the previous loaded page and make a toast message

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
}
