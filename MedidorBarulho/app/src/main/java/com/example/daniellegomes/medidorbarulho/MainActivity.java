package com.example.daniellegomes.medidorbarulho;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;



import org.w3c.dom.Text;

import at.abraxas.amarino.AmarinoIntent;

public class MainActivity extends AppCompatActivity {

    private static final String macAdress = "20:16:10:25:19:09";
    private SeekBar seekBarTime = null;
    private TextView txtValorTime = null;

    private SeekBar seekBar = null;
    private TextView txtValor = null;
    private DataReceiver meuDataReceiver = null;

    private int limiteBarulho = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        seekBarTime = (SeekBar)findViewById(R.id.skbValorTempo);
        txtValorTime = (TextView)findViewById(R.id.txtValorTempo);


        seekBar = (SeekBar)findViewById(R.id.skbValor);
        txtValor = (TextView)findViewById(R.id.txtValor);

        confuguraSeekbartime();
        confuguraSeekbar();
        registrarDataReceiver();
}
    private void confuguraSeekbartime() {
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBarTime, int i, boolean b) {
                        txtValorTime.setText(String.valueOf(i));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBarTime){
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBarTime){
                    }
        });
    }
    private void confuguraSeekbar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtValor.setText(String.valueOf(i));
                limiteBarulho = i;
                mudarSmile(limiteBarulho);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
            }
        });
    }

    /* Registra um BroadcastReceiver para saber se o arduínio mandou alguma informação para o app */
    public void registrarDataReceiver() {
        if(meuDataReceiver == null) {
            meuDataReceiver = new DataReceiver();
        }

        IntentFilter acoesIntent = new IntentFilter();
        acoesIntent.addAction(AmarinoIntent.ACTION_RECEIVED);
        this.registerReceiver(meuDataReceiver, acoesIntent);

    }


    /* Broadcast Receiver que avisa quando o arduíno enviar dados para o APP */
    private class DataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(AmarinoIntent.ACTION_RECEIVED)){
                Log.i("DataReceiver", "ACTION_RECEIVED");
                int tipoDado = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
                if(tipoDado == AmarinoIntent.STRING_EXTRA) {
                    String dado = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
                    //Log.i("DataReceiver", "Dado recebido do Arduino = " + dado);
                    //textValorPino.setText(dado);

                }
            }
        }
    }

    /* Mudar a imagem de acordo com o valor */
    public void mudarSmile(int num)
    {

        if(num > limiteBarulho)
        {


        } else if(num > 11 && num < limiteBarulho)
        {

        }

    }


}