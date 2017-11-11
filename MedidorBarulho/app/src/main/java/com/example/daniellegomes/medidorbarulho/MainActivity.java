package com.example.daniellegomes.medidorbarulho;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.Calendar;

import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class MainActivity extends AppCompatActivity implements AlarmManager.OnAlarmListener {

    //private static final String macAdress = "20:16:10:25:19:09"; //Dani
    private static final String macAdress = "20:16:10:25:18:43"; //Meu

    //Broadcasts
    private ConectionReceiver meuConectionReceiver = null;
    private DataReceiver meuDataReceiver = null;
    //private AlarmReceiver meuAlarmReceiver = null;

    //Constantes
    private final int ID_ALARM = 201711;
    // private final String ACTION_ID_ALARM = "alarm";
    //private final int VALOR_MIN_ANDROID = 0;
    //private final int VALOR_MAX_ANDROID = 100;

    private int limiteBarulho = 0;
    private int tempoMedicao = 0;

    private boolean MODE_MESURING = false;

    //Layout
    private SeekBar seekBarTime = null;
    private TextView txtValorTime = null;

    private SeekBar seekBarLimite = null;
    private TextView txtValorLimite = null;
    /* Mudar a imagem de acordo com o valor do ruído*/
    ImageView smiles;
    DonutProgress donut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fazer testes aqui pra saber se realmente conectou, dar algum feedback para pessoas que está usando
        Log.i("MainActivity", "Conectando no arduino");
        Amarino.connect(MainActivity.this, macAdress);

        seekBarTime = (SeekBar)findViewById(R.id.skbValorTempo);
        txtValorTime = (TextView)findViewById(R.id.txtValorTempo);


        seekBarLimite = (SeekBar)findViewById(R.id.skbValorLimite);
        txtValorLimite = (TextView)findViewById(R.id.txtValorLimite);

        smiles = (ImageView) findViewById(R.id.smile);
        donut = (DonutProgress) findViewById(R.id.donut_progress);
        donut.setSuffixText("dB");
        donut.setUnfinishedStrokeWidth(25);

        configurarSeekbarTime();
        configurarSeekbarLimite();
        registrarDataReceiver();
        registrarReceiverConectar();
        //registrarAlarmReceiver();

        MODE_MESURING = false;

    }

    @Override
    protected void onStop() {
        super.onStop();
        Amarino.disconnect(MainActivity.this, macAdress);
        MODE_MESURING = false;

        Log.i("MainActivity", "Desconectando do Arduíno");
    }

    private void configurarSeekbarTime() {
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBarTime, int i, boolean b) {
                txtValorTime.setText(String.valueOf(i));
                tempoMedicao = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBarTime){
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBarTime){
            }
        });
    }

    private void configurarSeekbarLimite() {
        seekBarLimite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtValorLimite.setText(String.valueOf(i));
                limiteBarulho = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
            }
        });
    }

    /**
     * Começar medição
     * @param v - Parâmetro obrigatório do botão
     */
    public void iniciarMedicao(View v) {
        Amarino.sendDataToArduino(MainActivity.this, macAdress,'I', limiteBarulho);
        registrarAlarm(tempoMedicao);
        MODE_MESURING = true;
    }

    /**
     * Parar medição
     * @param v - Parâmetro obrigatório do botão
     */
    public void pararMedicao(View v) {
        Log.i("MainActivity", "Parando medição pelo botão de PARAR");
        paraMedicao();
    }

    private void paraMedicao() {
        Amarino.sendDataToArduino(MainActivity.this, macAdress,'P', " ");
        MODE_MESURING = false;
        exibirResultadoFinal();
    }

    /**
     * Começar medição
     * @param valorMedidor - Valor que vem do arduino
     */
    private void alterarFeedback (int valorMedidor) {

        Log.i("MainActivity", "Entrei na função alterar feedback");

        donut.setProgress(valorMedidor);

        if (valorMedidor < (limiteBarulho/2)) {
            smiles.setImageResource(R.drawable.smilefeliz);
            donut.setFinishedStrokeColor(getResources().getColor(R.color.colorFeliz));

        } else if (valorMedidor > (limiteBarulho/2) && valorMedidor < limiteBarulho) {
            smiles.setImageResource(R.drawable.smilepreocupado);
            donut.setFinishedStrokeColor(getResources().getColor(R.color.colorPreocupada));
        } else if (valorMedidor >= limiteBarulho) {
            smiles.setImageResource(R.drawable.smiletriste);
            donut.setFinishedStrokeColor(getResources().getColor(R.color.colorTriste));
        }
    }

    /**
     *
     * @param valorAtual VAlor lido do sensor do Arduino
     * @return valor dentro do espectro apresentado no app
     */
    /*
    private int converterValorMedicaoAtual(String valorAtual) {

        Log.i("MainActivity", "Entrando em  converterValorMedicaoAtual");
        Log.i("MainActivity", "Parametro recebido: " + valorAtual);

        int valor = 0;

        //Verificar se a String é um número
        try {
            valor = Integer.parseInt(valorAtual);
        } catch (NumberFormatException e) {
            Log.i("MainActivity", "Valor passado pelo Arduino não é um número, setando para zero");
            valor = 0;
        }

        if (valor < VALOR_MIN_ARDUINO)
            valor = VALOR_MIN_ANDROID;
        else if (valor >= VALOR_MIN_ARDUINO && valor <= VALOR_MAX_ARDUINO)
            valor = valor - VALOR_MIN_ARDUINO;
        else
            valor = VALOR_MAX_ANDROID;

        return valor;

    }
    */

    /**
     * Mostrar na tela quantas vezes a medição passou do valor estabelecido
     */
    private void exibirResultadoFinal() {
        Toast.makeText(this, "TERMINOU!!!!!!", Toast.LENGTH_LONG).show();
    }


    private void registrarAlarm(int minutos) {

        Log.i("MainActivity", "Entrei em registrar alarm para despertar em : " + minutos);

        AlarmManager alarmManager;
        Intent intent;
        PendingIntent pendingIntent;
        Calendar calendar;

        calendar =  Calendar.getInstance();

        Log.i("MainActivity", "Calendar = " + calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" +
                calendar.get(Calendar.DAY_OF_MONTH));


        //para que o alarme avise depois de x minutos
        calendar.add(Calendar.MINUTE, minutos);

        Log.i("MainActivity", "Calendar com minutos = " + calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" +
                calendar.get(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

        // Obtém um alarm manager
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(getBaseContext().ALARM_SERVICE);

        // VERIFICAR AQUI SE PRECISA DE MAIS ALGUM PARAMETRO
        //intent = new Intent(this, AlarmReceiver.class);
        //intent.setAction(ACTION_ID_ALARM);

        // Obtém o pending intent
        //pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ID_ALARM,
        //        intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), "", this, null);

        Log.i("MainActivity", "Alarme registrado");


    }


    /*
     * Registra um BroadcastReceiver para saber se o app está conectado ao bluetooth do arduino.
     * Informa quando a conexão é estabelecida e quando ela é cancelada.
     *
     */
    private void registrarReceiverConectar() {
        if(meuConectionReceiver == null) {
            meuConectionReceiver = new ConectionReceiver();
        }

        IntentFilter acoesIntent = new IntentFilter();
        acoesIntent.addAction(AmarinoIntent.ACTION_CONNECTED);
        acoesIntent.addAction(AmarinoIntent.ACTION_DISCONNECTED);
        acoesIntent.addAction(AmarinoIntent.ACTION_CONNECTION_FAILED);
        acoesIntent.addAction(AmarinoIntent.ACTION_PAIRING_REQUESTED);
        this.registerReceiver(meuConectionReceiver, acoesIntent);

    }

    /* Registra um BroadcastReceiver para saber se o arduínio mandou alguma informação para o app */
    private void registrarDataReceiver() {
        if(meuDataReceiver == null) {
            meuDataReceiver = new DataReceiver();
        }

        IntentFilter acoesIntent = new IntentFilter();
        acoesIntent.addAction(AmarinoIntent.ACTION_RECEIVED);
        this.registerReceiver(meuDataReceiver, acoesIntent);

    }


    /* Registra um BroadcastReceiver para saber se o arduínio mandou alguma informação para o app */
    /*
    private void registrarAlarmReceiver() {
        if(meuAlarmReceiver == null) {
            meuAlarmReceiver = new AlarmReceiver();
        }

        IntentFilter acoesIntent = new IntentFilter();
        acoesIntent.addAction(ACTION_ID_ALARM);
        this.registerReceiver(meuAlarmReceiver, acoesIntent);

    }
    */

    @Override
    public void onAlarm() {
        Log.i("AlarmReceiver", "Entrei em onReceive do Alarm para parar a medição");
        // manda o arduino parar de medir
        paraMedicao();
    }


    /**************** DEFINIçÂO DAS CLASSES DE BROADCAST  *****************/


    /**
     *
     * Broadcast Receiver que avisa quando o arduíno enviar dados para o APP
     *
     * */
    private class DataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int valor = 0;
            if(intent.getAction().equals(AmarinoIntent.ACTION_RECEIVED)){
                Log.i("DataReceiver", "ACTION_RECEIVED");
                int tipoDado = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
                if(tipoDado == AmarinoIntent.STRING_EXTRA) {
                    String dado = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
                    Log.i("DataReceiver", "Dado recebido do Arduino = " + dado);

                    if(MODE_MESURING) { //altera feedback
                        //int valor = converterValorMedicaoAtual(dado);
                        //Verificar se a String é um número
                        try {
                            valor = Integer.parseInt(dado);
                        } catch (NumberFormatException e) {
                            Log.i("MainActivity", "Valor passado pelo Arduino não é um número, setando para zero");
                            valor = 0;
                        }

                        alterarFeedback(valor);
                    }
                }
            }
        }
    }

    /*
     *   Broadcast Receiver de Conexão com o Bluetooth
     */
    private class ConectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(AmarinoIntent.ACTION_CONNECTED)){
                Log.i("ConectionReceiver", "Conectei");
            } else {
                Log.i("ConectionReceiver", "Qualquer outro estado");
            }

        }
    }

    /**
     *
     * Broadcast Receiver para executar o que é pedido quando o alarm é chamado
     *
     */

    /*
    private class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            if(intent.getAction().equals(ACTION_ID_ALARM)) {
                Log.i("AlarmReceiver", "Entrei em onReceive do Alarm para parar a medição");
                // manda o arduino parar de medir
                paraMedicao();
            }
        }


    }
    */

}