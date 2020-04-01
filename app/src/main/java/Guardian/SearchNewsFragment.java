package Guardian;

import com.example.finalproject.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;




/**
 * A simple {@link Fragment} subclass.
 */
public class SearchNewsFragment extends Fragment {


    public SearchNewsFragment() {

    }

    private EditText editText;
    private ProgressBar bar;
    private ListArticleAdapter listArticleAdapter;
    private List<Article> articleList;


    /**
     * This method sets up the fragment with all the information and allows the user to click
     * on the url.
     * @param inflater The inflater to inflate the fragment.
     * @param container
     * @param savedInstanceState
     * @return The inflated fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_news, container, false);
        ImageButton imageButton = view.findViewById(R.id.imageButton);
        editText = view.findViewById(R.id.editText);
        bar = view.findViewById(R.id.progressBar);
        bar.setVisibility(View.GONE);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editText.getText().toString().trim();
                if (query.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter topic name to search!", Toast.LENGTH_SHORT).show();
                } else {
                    new SearchArticle(query).execute();
                }
            }
        });
        ListView listView = view.findViewById(R.id.listView);
        articleList = new ArrayList<>();
        listArticleAdapter = new ListArticleAdapter(getActivity(), articleList);
        listView.setAdapter(listArticleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), GuardianDetailActivity.class);
                intent.putExtra("article",articleList.get(position));
                startActivity(intent);
            }
        });

        String lastSearched = getLastSearched();
        if(!lastSearched.equals("")){
            editText.setText(lastSearched);
            new SearchArticle(lastSearched).execute();

        }
        return view;
    }

    /**
     * class used for Async Task that gets JSON Object from web pages
     */
    class SearchArticle extends AsyncTask<Void, Void, String> {

        /**
         * connect to web pages using URL and obtain JSON objects
         * @param strings
         * @return strings pulled from JSON objects
         */
        String link = "https://content.guardianapis.com/search?api-key=1fb36b70-1588-4259-b703-2570ea1fac6a&q=";
        String query;
        SearchArticle(String query) {
            this.query = query;
            try {
                this.link = link + URLEncoder.encode(query, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String data = "";
            try {
                URL mUrl = new URL(link);
                HttpURLConnection connection = (HttpURLConnection)
                        mUrl.openConnection();

                data = getString(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        /**
         * Update progress bar, list, and adapter
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            bar.setVisibility(View.GONE);
            if (s.length() == 0) {
                Toast.makeText(getActivity(), "failed!", Toast.LENGTH_SHORT).show();
            } else {
                try {

                    saveToSharedPreferences(query);

                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject response = jsonObject.getJSONObject("response");
                    String status = response.getString("status");
                    if (status.equals("ok")) {
                        articleList.clear();
                        JSONArray results = response.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject article = results.getJSONObject(i);
                            String id = article.getString("id");
                            String sectionName = article.getString("sectionName");
                            String webPublicationDate = article.getString("webPublicationDate");
                            String webUrl = article.getString("webUrl");
                            String webTitle = article.getString("webTitle");

                            Article articleData = new Article();
                            articleData.setId(id);
                            articleData.setSectionName(sectionName);

                            articleData.setTitle(webTitle);
                            articleData.setUrl(webUrl);
                            articleList.add(articleData);
                        }
                        listArticleAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private String getString(InputStream is) {
            String line = "";
            InputStreamReader isr = new
                    InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while (true) {
                try {
                    if ((line = br.readLine()) == null) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sb.append(line);
            }
            return sb.toString();
        }
    }


    /**
     * This method stores what the user last searched for in a file called FileName. It will load
     * what the user last searched for in the searchbar by default.
     * @param lastSearched What the user last searched for
     */
    private void saveToSharedPreferences(String lastSearched){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("guardianData", Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString("lastSearched",lastSearched).apply();
    }


    /**
     * This method displays a sharedPreference to get the last article search from database
     */
    private String getLastSearched(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("guardianData", Context.MODE_PRIVATE);
        String lastSearched = sharedPreferences.getString("lastSearched","");
        return lastSearched;
    }

}
