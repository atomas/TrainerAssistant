package air.foi.hr.trainerassistant.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import air.foi.hr.trainerassistant.HttpManager;
import air.foi.hr.trainerassistant.Izbornik;
import air.foi.hr.trainerassistant.JSONParser;
import air.foi.hr.trainerassistant.R;
import air.foi.hr.trainerassistant.RequestPackage;
import air.foi.hr.trainerassistant.adapter.RezultatiAdapter;
import air.foi.hr.trainerassistant.api.NavigationItem;
import air.foi.hr.trainerassistant.base.BaseFragment;
import air.foi.hr.trainerassistant.model.Atleticar;
import air.foi.hr.trainerassistant.model.Disciplina;
import air.foi.hr.trainerassistant.model.Rezultat;


public class RezultatFragment extends BaseFragment implements NavigationItem {

    private int position;
    private RecyclerView recyclerView;
    private RezultatiAdapter adapter;
    private List<Atleticar> atleticarList;
    private List<Disciplina> disciplinaList;
    private ProgressDialog pDialog;
    private int a;
    private Disciplina d;
    private String name;

    public RezultatFragment(){
        //konstruktor
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_rezultati;
    }

    @Override
    protected void init() {
        Log.d("Inicijalizacija", "Inicijalizacija");
        //inicijalizacija
        disciplinaList = new ArrayList<>();
        a=0;
        //dohvacanje podataka sa apija
        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri("http://atomas.comxa.com/rest/rezultat.php");
        p.setParam("id", String.valueOf(((Izbornik) getActivity()).getDisciplinaList().get(0).getId()));
        Update update = new Update();
        update.execute(p);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
    }

    @Override
    public String getItemName() {
        return "Pregled statistike";
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    private class Update extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            d = new Disciplina();
            for (Disciplina dr : ((Izbornik) getActivity()).getDisciplinaList()){
                Log.d("Ovo je " + String.valueOf(a), dr.getNaziv());
            }
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            String content = HttpManager.getData(params[0]);
            Log.d("Ovo je content", content);
            //parsiranje
            atleticarList = JSONParser.parseAtleticar(content);
            d.setNaziv(((Izbornik) getActivity()).getDisciplinaList().get(a).getNaziv());
            d.setAtleticarList(atleticarList);
            //dodavanje itema u disciplinelist
            disciplinaList.add(d);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            a++;
            Log.d("Ovo je a", String.valueOf(a));
            Log.d("Ovo je size", String.valueOf(((Izbornik) getActivity()).getDisciplinaList().size()));
            if (a == ((Izbornik) getActivity()).getDisciplinaList().size()) {
                Log.i("Usao sam u if", "if");
                pDialog.dismiss();
                //ako sam dodao sve discipline, i sve atleticare, prikazi ih u listi
                recyclerView = (RecyclerView) view.findViewById(R.id.rezultati_recyclerView);
                adapter = new RezultatiAdapter(getActivity(), disciplinaList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapter);
            } else {
                Log.i("Usao sam u else", "else");
                //postoji jos atleticara koji se trebaju dodati, zbog toga se ponovno poziva server
                //pDialog.dismiss();
                RequestPackage p = new RequestPackage();
                p.setMethod("POST");
                p.setUri("http://atomas.comxa.com/rest/rezultat.php");
                p.setParam("id", String.valueOf(((Izbornik) getActivity()).getDisciplinaList().get(a).getId()));
                Update update = new Update();
                update.execute(p);
            }
        }
    }

}
