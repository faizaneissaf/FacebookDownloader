package com.example.facebookdownloader;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.DOWNLOAD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class HomeFragment extends Fragment {
    Button btn_download,btn_paste;
    EditText et_link;
    ClipboardManager clipboardManager;
    DownloadManager downloadManager;

    //----Directory
    private String root_dir="/Facebook Downloads/";

    public HomeFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        btn_download=view.findViewById(R.id.btn_download);
        btn_paste=view.findViewById(R.id.btn_paste_link);
        et_link=view.findViewById(R.id.et_pasteLink);

        clipboardManager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
        downloadManager =(DownloadManager)getActivity().getSystemService(DOWNLOAD_SERVICE);

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                downloadFile();
                getFbData();
            }
        });
        btn_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clipData=clipboardManager.getPrimaryClip();
                ClipData.Item item=clipData.getItemAt(0);
                et_link.setText(item.getText().toString());
            }
        });


        return view;
    }

    private void getFbData() {
        URL url = null;
        try {
            url = new URL(et_link.getText().toString());
            String host=url.getHost();
            if (host.contains("facebook.com")){
                new CallGetFBData().execute(et_link.getText().toString());
            }else{
                Toast.makeText(getActivity(), "Please Paste link of Facebook Video", Toast.LENGTH_SHORT).show();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    private void downloadVideo(String download_path,String destination_path,Context context,String file_name) {
        Toast.makeText(context, "Download Started....", Toast.LENGTH_SHORT).show();
        Uri uri=Uri.parse(download_path);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(file_name);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,destination_path+file_name);
        ((DownloadManager)context.getSystemService(DOWNLOAD_SERVICE)).enqueue(request);

    }

    //-------Download File
    private void downloadFile() {
        String getUrl=et_link.getText().toString();
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(getUrl+".mp4"));
        String title= URLUtil.guessFileName(getUrl,null,null);
        request.setTitle(title);
        request.setDescription("File Downloading..");
        String cookie= CookieManager.getInstance().getCookie(getUrl);
        request.addRequestHeader("cookie",cookie);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                title);
        request.setAllowedOverMetered(true);
        request.setAllowedOverRoaming(true);

        downloadManager.enqueue(request);
        Toast.makeText(getActivity(), "Download Started", Toast.LENGTH_SHORT).show();
    }

    class CallGetFBData extends AsyncTask<String , Void, Document>{

        Document fbDoc;

        @Override
        protected Document doInBackground(String... strings) {
            try {
                fbDoc= Jsoup.connect(strings[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fbDoc;
        }

        @Override
        protected void onPostExecute(Document document) {
            String  videoUrl=document.select("meta[property=\"og:video\"]")
                    .last().attr("content");
            if (!videoUrl.equals("")){
                downloadVideo(videoUrl,root_dir,getActivity(),"facebook"+System.currentTimeMillis()+".mp4");
            }
        }
    }
}