package com.amazonaws.kinesisvideo.demoapp.activity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.amazonaws.kinesisvideo.demoapp.R;
import com.amazonaws.kinesisvideo.signaling.SignalingListener;
import com.amazonaws.kinesisvideo.signaling.model.Event;
import com.amazonaws.kinesisvideo.signaling.model.Message;
import com.amazonaws.kinesisvideo.signaling.tyrus.SignalingServiceWebSocketClient;
import com.amazonaws.kinesisvideo.webrtc.KinesisVideoPeerConnection;
import com.amazonaws.kinesisvideo.webrtc.KinesisVideoSdpObserver;

import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceServer;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RTCStats;
import org.webrtc.RTCStatsCollectorCallback;
import org.webrtc.RTCStatsReport;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class WebRtcActivity extends AppCompatActivity {
    private static final String TAG = "KVSWebRtcActivity";


    private static String WSS_SIGN_URL =
            "wss://v-b35a547e.kinesisvideo.sa-east-1.amazonaws.com/?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-ChannelARN=arn%253Aaws%253Akinesisvideo%253Asa-east-1%253A183521707800%253Achannel%252F227d64963babf67b8df489dd8cee8a453d23737f6b30706eb90f2fa2a657909b%252F1635245940094&X-Amz-ClientId=229285051375271936&X-Amz-Credential=ASIASVOV7A4MMGTRN3MX%252F20220122%252Fsa-east-1%252Fkinesisvideo%252Faws4_request&X-Amz-Date=20220122T192033Z&X-Amz-Expires=299&X-Amz-Security-Token=FwoGZXIvYXdzEDUaDG%252FrTTRcgEIydtnuHiKCAdvo6YD1AmS2suQrm7PZBLY4WRxhE2T28WRXiIGaisi98YLT79zJQuKF9YS4b7HUVoNYZP5vlFbU4tP5yb0YA%252BUve5TaxU64mBz76zMsVAaKoO14htUKN2fZeA24ke30i9jQbApiBQ39XUWtNIoUiwvf%252B6bR9d5p0sfMVlNBuhXC494ogbKxjwYyKNi9yuNvf8%252FOYq1%252BycbHH%252FC8VrjcFSgwEnvYt6SE3Q6qebCqDLUdzUc%253D&X-Amz-SignedHeaders=host&X-Amz-Signature=36acef32c3e13acac51fde17ad1873dfdbee32f20cd8dc0519a98ba90b26cd4c"
    ;

    private static String PASSWORD1 = "xeSK7TocJbyw0K8W+q62j9KheZy2aUp22orlWPgRlNc=";

    private static List<String> URIS1 = Stream.of(
            "turn:18-231-48-130.t-c949c048.kinesisvideo.sa-east-1.amazonaws.com:443?transport=udp",
            "turns:18-231-48-130.t-c949c048.kinesisvideo.sa-east-1.amazonaws.com:443?transport=udp",
            "turns:18-231-48-130.t-c949c048.kinesisvideo.sa-east-1.amazonaws.com:443?transport=tcp"
    ).collect(Collectors.toList());

    private static String USERNAME1 =
            "1642879533:djE6YXJuOmF3czpraW5lc2lzdmlkZW86c2EtZWFzdC0xOjE4MzUyMTcwNzgwMDpjaGFubmVsLzIyN2Q2NDk2M2JhYmY2N2I4ZGY0ODlkZDhjZWU4YTQ1M2QyMzczN2Y2YjMwNzA2ZWI5MGYyZmEyYTY1NzkwOWIvMTYzNTI0NTk0MDA5NA=="
            ;

    private static String PASSWORD2 = "9GJYHWqfs70okgInZBsIfeb2EVQxTn7AFx7EeLcxWMY=";

    private static List<String> URIS2 = Stream.of(
            "turn:15-228-226-166.t-c949c048.kinesisvideo.sa-east-1.amazonaws.com:443?transport=udp",
            "turns:15-228-226-166.t-c949c048.kinesisvideo.sa-east-1.amazonaws.com:443?transport=udp",
            "turns:15-228-226-166.t-c949c048.kinesisvideo.sa-east-1.amazonaws.com:443?transport=tcp"
    ).collect(Collectors.toList());

    private static String USERNAME2 =
            "1642879533:djE6YXJuOmF3czpraW5lc2lzdmlkZW86c2EtZWFzdC0xOjE4MzUyMTcwNzgwMDpjaGFubmVsLzIyN2Q2NDk2M2JhYmY2N2I4ZGY0ODlkZDhjZWU4YTQ1M2QyMzczN2Y2YjMwNzA2ZWI5MGYyZmEyYTY1NzkwOWIvMTYzNTI0NTk0MDA5NA=="
            ;

    private static final String CHANNEL_ID = "WebRtcDataChannel";
    private static final boolean ENABLE_INTEL_VP8_ENCODER = true;
    private static final boolean ENABLE_H264_HIGH_PROFILE = true;

    private static volatile SignalingServiceWebSocketClient client;
    private PeerConnectionFactory peerConnectionFactory;

    private VideoSource videoSource;

    private AudioManager audioManager;
    private int originalAudioMode;
    private boolean originalSpeakerphoneOn;

    private AudioTrack localAudioTrack;

    private SurfaceViewRenderer localView;
    private SurfaceViewRenderer remoteView;

    private PeerConnection localPeer;

    private EglBase rootEglBase = null;

    private final List<IceServer> peerIceServers = new ArrayList<>();

    private boolean gotException = false;

    private String recipientClientId;

    private int mNotificationId = 0;

    private boolean master = true;

    private EditText dataChannelText = null;
    private Button sendDataChannelButton = null;

    private String mClientId;

    private String mRegion;

    // Map to keep track of established peer connections by IDs
    private HashMap<String, PeerConnection> peerConnectionFoundMap = new HashMap<String, PeerConnection>();
    // Map to keep track of ICE candidates received for a client ID before peer connection is established
    private HashMap<String, Queue<IceCandidate>> pendingIceCandidatesMap = new HashMap<String, Queue<IceCandidate>>();

    private void initWsConnection() {

        final String wsHost = WSS_SIGN_URL;

        final SignalingListener signalingListener = new SignalingListener() {

            @Override
            public void onSdpOffer(final Event offerEvent) {
                Log.d(TAG, "Received SDP Offer: Setting Remote Description ");

                final String sdp = Event.parseOfferEvent(offerEvent);

                localPeer.setRemoteDescription(new KinesisVideoSdpObserver(),
                        new SessionDescription(SessionDescription.Type.OFFER, sdp));

                recipientClientId = offerEvent.getSenderClientId();
                Log.d(TAG, "Received SDP offer for client ID: " + recipientClientId + ".Creating answer");

                createSdpAnswer();
            }

            @Override
            public void onSdpAnswer(final Event answerEvent) {

                Log.d(TAG, "SDP answer received from signaling");

                final String sdp = Event.parseSdpEvent(answerEvent);

                final SessionDescription sdpAnswer = new SessionDescription(SessionDescription.Type.ANSWER, sdp);

                localPeer.setRemoteDescription(new KinesisVideoSdpObserver(), sdpAnswer);
                Log.d(TAG, "Answer Client ID: " + answerEvent.getSenderClientId());
                peerConnectionFoundMap.put(answerEvent.getSenderClientId(), localPeer);
                // Check if ICE candidates are available in the queue and add the candidate
                handlePendingIceCandidates(answerEvent.getSenderClientId());

            }

            @Override
            public void onIceCandidate(Event message) {

                Log.d(TAG, "Received IceCandidate from remote ");

                final IceCandidate iceCandidate = Event.parseIceCandidate(message);

                if (iceCandidate != null) {
                    checkAndAddIceCandidate(message, iceCandidate);
                } else {
                    Log.e(TAG, "Invalid Ice candidate");
                }
            }

            @Override
            public void onError(Event errorMessage) {

                Log.e(TAG, "Received error message" + errorMessage);

            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, "Signaling client returned exception " + e.getMessage());
                gotException = true;
            }
        };


        if (wsHost != null) {
            try {
                client = new SignalingServiceWebSocketClient(wsHost, signalingListener, Executors.newFixedThreadPool(10));

                Log.d(TAG, "Client connection " + (client.isOpen() ? "Successful" : "Failed"));
            } catch (Exception e) {
                gotException = true;
            }

            if (isValidClient()) {

                Log.d(TAG, "Client connected to Signaling service " + client.isOpen());

                if (!master) {
                    Log.d(TAG, "Signaling service is connected: " +
                            "Sending offer as viewer to remote peer"); // Viewer

                    createSdpOffer();
                }
            } else {
                Log.e(TAG, "Error in connecting to signaling service");
                gotException = true;
            }
        }
    }

    private boolean isValidClient() {
        return client != null && client.isOpen();
    }

    private void handlePendingIceCandidates(String clientId) {
        // Add any pending ICE candidates from the queue for the client ID
        Log.d(TAG, "Pending ice candidates found? " + pendingIceCandidatesMap.get(clientId));
        Queue<IceCandidate> pendingIceCandidatesQueueByClientId = pendingIceCandidatesMap.get(clientId);
        while (pendingIceCandidatesQueueByClientId != null && !pendingIceCandidatesQueueByClientId.isEmpty()) {
            final IceCandidate iceCandidate = pendingIceCandidatesQueueByClientId.peek();
            final PeerConnection peer = peerConnectionFoundMap.get(clientId);
            final boolean addIce = peer.addIceCandidate(iceCandidate);
            Log.d(TAG, "Added ice candidate after SDP exchange " + iceCandidate + " " + (addIce ? "Successfully" : "Failed"));
            pendingIceCandidatesQueueByClientId.remove();
        }
        // After sending pending ICE candidates, the client ID's peer connection need not be tracked
        pendingIceCandidatesMap.remove(clientId);
    }

    private void checkAndAddIceCandidate(Event message, IceCandidate iceCandidate) {
        // if answer/offer is not received, it means peer connection is not found. Hold the received ICE candidates in the map.

        if (!peerConnectionFoundMap.containsKey(message.getSenderClientId())) {
            Log.d(TAG, "SDP exchange is not complete. Ice candidate " + iceCandidate + " + added to pending queue");

            // If the entry for the client ID already exists (in case of subsequent ICE candidates), update the queue
            if (pendingIceCandidatesMap.containsKey(message.getSenderClientId())) {
                Queue<IceCandidate> pendingIceCandidatesQueueByClientId = pendingIceCandidatesMap.get(message.getSenderClientId());
                pendingIceCandidatesQueueByClientId.add(iceCandidate);
                pendingIceCandidatesMap.put(message.getSenderClientId(), pendingIceCandidatesQueueByClientId);
            }

            // If the first ICE candidate before peer connection is received, add entry to map and ICE candidate to a queue
            else {
                Queue<IceCandidate> pendingIceCandidatesQueueByClientId = new LinkedList<>();
                pendingIceCandidatesQueueByClientId.add(iceCandidate);
                pendingIceCandidatesMap.put(message.getSenderClientId(), pendingIceCandidatesQueueByClientId);
            }
        }

        // This is the case where peer connection is established and ICE candidates are received for the established
        // connection
        else {
            Log.d(TAG, "Peer connection found already");
            // Remote sent us ICE candidates, add to local peer connection
            final PeerConnection peer = peerConnectionFoundMap.get(message.getSenderClientId());
            final boolean addIce = peer.addIceCandidate(iceCandidate);

            Log.d(TAG, "Added ice candidate " + iceCandidate + " " + (addIce ? "Successfully" : "Failed"));
        }
    }

    @Override
    protected void onDestroy() {
        Thread.setDefaultUncaughtExceptionHandler(null);

        audioManager.setMode(originalAudioMode);
        audioManager.setSpeakerphoneOn(originalSpeakerphoneOn);

        if (rootEglBase != null) {
            rootEglBase.release();
            rootEglBase = null;
        }

        if (remoteView != null) {
            remoteView.release();
            remoteView = null;
        }

        if (localPeer != null) {
            localPeer.dispose();
            localPeer = null;
        }

        if (videoSource != null) {
            videoSource.dispose();
            videoSource = null;
        }

        if (localView != null) {
            localView.release();
            localView = null;
        }

        if (client != null) {
            client.disconnect();
            client = null;
        }
        peerConnectionFoundMap.clear();
        pendingIceCandidatesMap.clear();

        finish();

        super.onDestroy();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Start websocket after adding local audio/video tracks
        initWsConnection();

        if (!gotException && isValidClient()) {
            Toast.makeText(this, "Signaling Connected", Toast.LENGTH_LONG).show();
        } else {
            notifySignalingConnectionFailed();
        }
    }

    private void notifySignalingConnectionFailed() {
        finish();
        Toast.makeText(this, "Connection error to signaling", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mClientId = UUID.randomUUID().toString();

        master = false;

        ArrayList<String> mUserNames = new ArrayList<String>();
        mUserNames.add(USERNAME1);
        mUserNames.add(USERNAME2);

        ArrayList<String> mPasswords = new ArrayList<String>();
        mPasswords.add(PASSWORD1);
        mPasswords.add(PASSWORD2);


        ArrayList<List<String>> mUrisList = new ArrayList<List<String>>();
        mUrisList.add(URIS1);
        mUrisList.add(URIS2);

        mRegion = "sa-east-1";

        rootEglBase = EglBase.create();

        //TODO: add ui to control TURN only option

        PeerConnection.IceServer stun = PeerConnection
                .IceServer
                .builder(String.format("stun:stun.kinesisvideo.%s.amazonaws.com:443", mRegion))
                .createIceServer();

        peerIceServers.add(stun);

        for (int i = 0; i < mUrisList.size(); i++) {
            String turnServer = mUrisList.get(i).toString();
            if (turnServer != null) {
                IceServer iceServer = IceServer.builder(turnServer.replace("[", "").replace("]", ""))
                        .setUsername(mUserNames.get(i))
                        .setPassword(mPasswords.get(i))
                        .createIceServer();
                Log.d(TAG, "IceServer details (TURN) = " + iceServer.toString());
                peerIceServers.add(iceServer);
            }
        }

        setContentView(R.layout.activity_webrtc_main);

        PeerConnectionFactory.initialize(PeerConnectionFactory
                .InitializationOptions
                .builder(this)
                .createInitializationOptions());

        peerConnectionFactory =
                PeerConnectionFactory.builder()
                        .setVideoDecoderFactory(new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext()))
                        .setVideoEncoderFactory(new DefaultVideoEncoderFactory(rootEglBase.getEglBaseContext(), ENABLE_INTEL_VP8_ENCODER, ENABLE_H264_HIGH_PROFILE))
                        .createPeerConnectionFactory();

        // Local video view
        localView = findViewById(R.id.local_view);
        localView.init(rootEglBase.getEglBaseContext(), null);
        localView.setEnableHardwareScaler(true);


        videoSource = peerConnectionFactory.createVideoSource(false);
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create(Thread.currentThread().getName(), rootEglBase.getEglBaseContext());

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        originalAudioMode = audioManager.getMode();
        originalSpeakerphoneOn = audioManager.isSpeakerphoneOn();

        remoteView = findViewById(R.id.remote_view);
        remoteView.init(rootEglBase.getEglBaseContext(), null);

        dataChannelText = findViewById(R.id.data_channel_text);
        sendDataChannelButton = findViewById(R.id.send_data_channel_text);

        createNotificationChannel();
    }


    private void createLocalPeerConnection() {

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(peerIceServers);

        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.ENABLED;

        localPeer = peerConnectionFactory.createPeerConnection(rtcConfig, new KinesisVideoPeerConnection() {

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {

                super.onIceCandidate(iceCandidate);

                Message message = createIceCandidateMessage(iceCandidate);
                Log.d(TAG, "Sending IceCandidate to remote peer " + iceCandidate.toString());
                client.sendIceCandidate(message);  /* Send to Peer */

            }

            @Override
            public void onAddStream(MediaStream mediaStream) {

                super.onAddStream(mediaStream);

                Log.d(TAG, "Adding remote video stream (and audio) to the view");

                addRemoteStreamToVideoView(mediaStream);
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                super.onDataChannel(dataChannel);

                dataChannel.registerObserver(new DataChannel.Observer() {
                    @Override
                    public void onBufferedAmountChange(long l) {
                        // no op on receiver side
                    }

                    @Override
                    public void onStateChange() {
                        Log.d(TAG, "Remote Data Channel onStateChange: state: " + dataChannel.state().toString());
                    }

                    @Override
                    public void onMessage(DataChannel.Buffer buffer) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                byte[] bytes;
                                if (buffer.data.hasArray()) {
                                    bytes = buffer.data.array();
                                } else {
                                    bytes = new byte[buffer.data.remaining()];
                                    buffer.data.get(bytes);
                                }

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                                R.mipmap.ic_launcher))
                                        .setContentTitle("Message from Peer!")
                                        .setContentText(new String(bytes, Charset.defaultCharset()))
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        .setAutoCancel(true);
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                // notificationId is a unique int for each notification that you must define
                                notificationManager.notify(mNotificationId++, builder.build());

                                Toast.makeText(getApplicationContext(), "New message from peer, check notification.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        if (localPeer != null) {

            localPeer.getStats(new RTCStatsCollectorCallback() {

                @Override
                public void onStatsDelivered(RTCStatsReport rtcStatsReport) {

                    Map<String, RTCStats> statsMap = rtcStatsReport.getStatsMap();

                    Set<Map.Entry<String, RTCStats>> entries = statsMap.entrySet();

                    for (Map.Entry<String, RTCStats> entry : entries) {

                        Log.d(TAG, "Stats: " + entry.getKey() + " ," + entry.getValue());

                    }
                }
            });
        }

        addDataChannelToLocalPeer();
    }

    private Message createIceCandidateMessage(IceCandidate iceCandidate) {
        String sdpMid = iceCandidate.sdpMid;
        int sdpMLineIndex = iceCandidate.sdpMLineIndex;
        String sdp = iceCandidate.sdp;

        String messagePayload =
                "{\"candidate\":\""
                        + sdp
                        + "\",\"sdpMid\":\""
                        + sdpMid
                        + "\",\"sdpMLineIndex\":"
                        + sdpMLineIndex
                        + "}";

        String senderClientId = (master) ? "" : mClientId;

        return new Message("ICE_CANDIDATE", recipientClientId, senderClientId,
                new String(Base64.encode(messagePayload.getBytes(),
                        Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP)));
    }

    private void addDataChannelToLocalPeer() {
        Log.d(TAG, "Data channel addDataChannelToLocalPeer");
        DataChannel localDataChannel = localPeer.createDataChannel("data-channel-of-" + mClientId, new DataChannel.Init());
        localDataChannel.registerObserver(new DataChannel.Observer() {
            @Override
            public void onBufferedAmountChange(long l) {
                Log.d(TAG, "Local Data Channel onBufferedAmountChange called with amount " + l);
            }

            @Override
            public void onStateChange() {
                Log.d(TAG, "Local Data Channel onStateChange: state: " + localDataChannel.state().toString());

                if (sendDataChannelButton != null) {
                    runOnUiThread(() -> {
                        if (localDataChannel.state() == DataChannel.State.OPEN) {
                            sendDataChannelButton.setEnabled(true);
                        } else {
                            sendDataChannelButton.setEnabled(false);
                        }
                    });
                }
            }

            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                // Send out data, no op on sender side
            }
        });

        sendDataChannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localDataChannel.send(new DataChannel.Buffer(
                        ByteBuffer.wrap(dataChannelText.getText().toString()
                                .getBytes(Charset.defaultCharset())), false));
                dataChannelText.setText("");
            }
        });
    }

    // when mobile sdk is viewer
    private void createSdpOffer() {

        MediaConstraints sdpMediaConstraints = new MediaConstraints();

        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));

        if (localPeer == null) {
            createLocalPeerConnection();
        }

        localPeer.createOffer(new KinesisVideoSdpObserver() {

            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {

                super.onCreateSuccess(sessionDescription);

                localPeer.setLocalDescription(new KinesisVideoSdpObserver(), sessionDescription);

                Message sdpOfferMessage = Message.createOfferMessage(sessionDescription, mClientId);

                if (isValidClient()) {
                    client.sendSdpOffer(sdpOfferMessage);
                } else {
                    notifySignalingConnectionFailed();
                }
            }
        }, sdpMediaConstraints);
    }

    // when local is set to be the master
    private void createSdpAnswer() {

        localPeer.createAnswer(new KinesisVideoSdpObserver() {

            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "Creating answer : success");
                super.onCreateSuccess(sessionDescription);
                localPeer.setLocalDescription(new KinesisVideoSdpObserver(), sessionDescription);
                Message answer = Message.createAnswerMessage(sessionDescription, master, recipientClientId);
                client.sendSdpAnswer(answer);

                peerConnectionFoundMap.put(recipientClientId, localPeer);
                handlePendingIceCandidates(recipientClientId);
            }
        }, new MediaConstraints());

    }

    private void addRemoteStreamToVideoView(MediaStream stream) {

        final VideoTrack remoteVideoTrack = stream.videoTracks != null && stream.videoTracks.size() > 0 ? stream.videoTracks.get(0) : null;

        AudioTrack remoteAudioTrack = stream.audioTracks != null && stream.audioTracks.size() > 0 ? stream.audioTracks.get(0) : null;

        if (remoteAudioTrack != null) {
            remoteAudioTrack.setEnabled(true);
            Log.d(TAG, "remoteAudioTrack received: State=" + remoteAudioTrack.state().name());
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setSpeakerphoneOn(true);
        }

        if (remoteVideoTrack != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d(TAG, "remoteVideoTrackId=" + remoteVideoTrack.id() + " videoTrackState=" + remoteVideoTrack.state());
                        resizeLocalView();
                        remoteVideoTrack.addSink(remoteView);
                        resizeRemoteView();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in setting remote video view" + e);
                    }
                }
            });
        } else {
            Log.e(TAG, "Error in setting remote track");
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void resizeLocalView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        final ViewGroup.LayoutParams lp = localView.getLayoutParams();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        lp.height = (int) (displayMetrics.heightPixels * 0.25);
        lp.width = (int) (displayMetrics.widthPixels * 0.25);
        localView.setLayoutParams(lp);
        localView.setOnTouchListener(new View.OnTouchListener() {
            private final int mMarginRight = displayMetrics.widthPixels;
            private final int mMarginBottom = displayMetrics.heightPixels;
            private int deltaOfDownXAndMargin, deltaOfDownYAndMargin;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int X = (int) motionEvent.getRawX();
                final int Y = (int) motionEvent.getRawY();
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) lp;

                        deltaOfDownXAndMargin = X + lParams.rightMargin;
                        deltaOfDownYAndMargin = Y + lParams.bottomMargin;

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) lp;

                        layoutParams.rightMargin = deltaOfDownXAndMargin - X;
                        layoutParams.bottomMargin = deltaOfDownYAndMargin - Y;

                        // shouldn't be out of screen
                        if (layoutParams.rightMargin >= mMarginRight - lp.width) {
                            layoutParams.rightMargin = mMarginRight - lp.width;
                        }

                        if (layoutParams.bottomMargin >= mMarginBottom - lp.height) {
                            layoutParams.bottomMargin = mMarginBottom - lp.height;
                        }

                        if (layoutParams.rightMargin <= 0) {
                            layoutParams.rightMargin = 0;
                        }

                        if (layoutParams.bottomMargin <= 0) {
                            layoutParams.bottomMargin = 0;
                        }

                        localView.setLayoutParams(layoutParams);
                }
                return false;
            }
        });
    }

    private void resizeRemoteView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            final ViewGroup.LayoutParams lp = remoteView.getLayoutParams();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            lp.height = (int) (displayMetrics.heightPixels * 0.75);
            lp.width = (int) (displayMetrics.widthPixels * 0.75);
            remoteView.setLayoutParams(lp);
            localView.bringToFront();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.data_channel_notification);
            String description = getString(R.string.data_channel_notification_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
