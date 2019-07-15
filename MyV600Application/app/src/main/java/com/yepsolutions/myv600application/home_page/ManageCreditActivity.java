package com.yepsolutions.myv600application.home_page;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.yepsolutions.myv600application.LoginActivity;
import com.yepsolutions.myv600application.MainActivity;
import com.yepsolutions.myv600application.PrinterActivity;
import com.yepsolutions.myv600application.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Printer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class ManageCreditActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private ImageView buy_ticket;
    /*public PrinterActivity printer = new PrinterActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_credit);

        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buy_ticket = (ImageView) findViewById(R.id.id_img_buy_ticket);

        buy_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    printer.printData();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }*/




    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    TextView lblPrinterName;
    EditText textBox;
    ImageView img_Ticket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_credit);

        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        lblPrinterName = (TextView) findViewById(R.id.lblPrinterName);
        img_Ticket = (ImageView) findViewById(R.id.id_img_buy_ticket);

        img_Ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    printData();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

    }

    void FindBluetoothDevice(){

        try{

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter==null){
                lblPrinterName.setText("No Bluetooth Adapter found");
            }
            if(bluetoothAdapter.isEnabled()){
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT,0);
            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if(pairedDevice.size()>0){
                for(BluetoothDevice pairedDev:pairedDevice){

                    // My Bluetoth printer name is BTP_F09F1A
                    if(pairedDev.getName().equals("SW_54BA")){
                        bluetoothDevice=pairedDev;
                        lblPrinterName.setText("Bluetooth Printer Attached: "+pairedDev.getName());
                        break;
                    }
                }
            }

            lblPrinterName.setText("Bluetooth Printer Attached");
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    // Open Bluetooth Printer

    void openBluetoothPrinter() throws IOException{
        try{

            //Standard uuid from string //
            UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket=bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
            bluetoothSocket.connect();
            outputStream=bluetoothSocket.getOutputStream();
            inputStream=bluetoothSocket.getInputStream();

            beginListenData();

        }catch (Exception ex){

        }
    }

    void beginListenData(){
        try{

            final Handler handler =new Handler();
            final byte delimiter=10;
            stopWorker =false;
            readBufferPosition=0;
            readBuffer = new byte[1024];

            thread=new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker){
                        try{
                            int byteAvailable = inputStream.available();
                            if(byteAvailable>0){
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for(int i=0; i<byteAvailable; i++){
                                    byte b = packetByte[i];
                                    if(b==delimiter){
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer,0,
                                                encodedByte,0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte,"US-ASCII");
                                        readBufferPosition=0;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                lblPrinterName.setText(data);
                                            }
                                        });
                                    }else{
                                        readBuffer[readBufferPosition++]=b;
                                    }
                                }
                            }
                        }catch(Exception ex){
                            stopWorker=true;
                        }
                    }

                }
            });

            thread.start();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // Printing Text to Bluetooth Printer //
    void printData() throws  IOException{
        try{

            String msg = "Imprimindo\n" +
                    ".....................................................................\n" +
                    "Passagem.....................................................R$ 12,75\n" +
                    "De Itajuba para Sao Lourenco\n"+
                    "Fim da impressao\n.\n.";
            msg+="\n";
            outputStream.write(msg.getBytes());
            lblPrinterName.setText("Printing Text...");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // Disconnect Printer //
    void disconnectBT() throws IOException{
        try {
            stopWorker=true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            lblPrinterName.setText("Printer Disconnected.");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public void connectBT() throws IOException {
        try {
            FindBluetoothDevice();
            openBluetoothPrinter();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ticket_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_connect:
                try {
                    connectBT();
                } catch (IOException e) {
                    Toast.makeText(ManageCreditActivity.this, "Erro ao conectar", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                return true;
            case R.id.id_action_disconnect:
                try {
                    disconnectBT();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

}