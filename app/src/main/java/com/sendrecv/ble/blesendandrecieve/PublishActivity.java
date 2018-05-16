package com.sendrecv.ble.blesendandrecieve;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PublishActivity extends AppCompatActivity {

    Button pubConnect,pubDisconnect,pubPub;
    EditText messagemqtt;
    String clientId;
    MqttAndroidClient client;
    MqttConnectOptions options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        pubConnect=(Button)findViewById(R.id.pubConnect);
        pubPub=(Button)findViewById(R.id.pubpub);
        pubDisconnect=(Button)findViewById(R.id.pubDisconnect);
        messagemqtt=(EditText)findViewById(R.id.messagemqtt) ;
        clientId = MqttClient.generateClientId();


        pubConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client = new MqttAndroidClient(getApplicationContext(), "tcp://m11.cloudmqtt.com:16515", clientId);
                options = new MqttConnectOptions();
                options.setUserName("vsptbtrg");
                options.setPassword("Zb5IXem-B2Rw".toCharArray());
                try {
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.d("Connection", "Connected to Broker ");
                            //Toast.makeText(getApplicationContext(),"ConnectiontoMQTTBrokerMade", Toast.LENGTH_SHORT).show();
                            pubConnect.setVisibility(View.INVISIBLE);
                            pubDisconnect.setVisibility(View.VISIBLE);
                            pubPub.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            //Toast.makeText(getApplicationContext(),"ConnectiontoMQTTBrokerRejected", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(),exception.toString(),Toast.LENGTH_LONG).show();
                            Log.d("Connection", "Unable to connect to Broker");
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        pubDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
            }
        });

        pubPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String payload = messagemqtt.getText().toString();
                byte[] encodedPayload;
                try {
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(getString(R.string.topic), message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pubConnect.getVisibility()==View.INVISIBLE)
            disconnect();
    }

    public void disconnect()
    {
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    pubPub.setVisibility(View.INVISIBLE);
                    pubDisconnect.setVisibility(View.INVISIBLE);
                    pubConnect.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}