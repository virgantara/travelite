package com.keltech.travel;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.keltech.travel.com.keltech.travel.helper.CalcHelper;
import com.keltech.travel.modules.AbstractRouting;
import com.keltech.travel.modules.Constants;
import com.keltech.travel.modules.PlaceAutoCompleteAdapter;
import com.keltech.travel.modules.Route;
import com.keltech.travel.modules.RouteException;
import com.keltech.travel.modules.Routing;
import com.keltech.travel.modules.RoutingListener;
import com.keltech.travel.modules.Util;
import com.tgwarrior.travelmockup.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PickUpActivity extends FragmentActivity implements

        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        RoutingListener {

    @BindView(R.id.order)
    LinearLayout order;
    @BindView(R.id.cancel)
    LinearLayout cancel;
    @BindView(R.id.pickUp)
    LinearLayout pickUp;
    @BindView(R.id.findPath)
    ImageView findPath;
    @BindView(R.id.etOrigin)
    AutoCompleteTextView etOrigin;
    @BindView(R.id.etDestination)
    AutoCompleteTextView etDestination;

    @BindView(R.id.tvDistance)
    TextView tvDistance;

    @BindView(R.id.tvDuration)
    TextView tvDuration;


    TextView lblJarak;
    TextView lblTotal;
    Button btnSubmitKritik;


    private ProgressDialog progressDialog = null;
    private GoogleMap gMap = null;
    private PlaceAutoCompleteAdapter mAutoAdapter = null;
    protected GoogleApiClient mGoogleApiClient = null;

    protected LatLng start;
    protected LatLng end;

    private static final LatLngBounds BOUNDS_KEDIRI = new LatLngBounds(
            new LatLng(-7.822840, 112.011864),
            new LatLng(-7.822840, 112.011864)
    );

    private static final String LOG_TAG = "MyActivity";

    private List<Polyline> polylines = null;
    private static final int[] COLORS = new int[]{
            R.color.primary_dark,
            R.color.primary,
            R.color.primary_light,
            R.color.accent,
            R.color.primary_dark_material_light
    };


    private MarkerOptions mePosition = null;

    private int drivingMode = 0;
    private Dialog dialog = null;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up);
        ButterKnife.bind(this);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.rincian_harga);
        dialog.setTitle("Rincian Harga");

        lblJarak = (TextView) dialog.findViewById(R.id.lblJarak);
        lblTotal = (TextView) dialog.findViewById(R.id.lblTotal);

        btnSubmitKritik = (Button) dialog.findViewById(R.id.btnSubmit);
        btnSubmitKritik.setOnClickListener(btnSubmitOnClick);

        Bundle bundle = getIntent().getExtras();

        drivingMode = bundle.getInt(Constants.FROM);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //gpsTracker = new GPSTracker(this);
        MapsInitializer.initialize(this);
        mGoogleApiClient.connect();
        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (map == null) {
            map = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map, map).commit();
        }

        polylines = new ArrayList<Polyline>();
        gMap = map.getMap();

        mAutoAdapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_KEDIRI, null);



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        LatLng kediriPos = new LatLng(location.getLatitude(), location.getLongitude());

        mePosition = new MarkerOptions();
        mePosition.position(kediriPos);
        mePosition.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
        gMap.addMarker(mePosition);

        gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                LatLngBounds bounds = gMap.getProjection().getVisibleRegion().latLngBounds;
                mAutoAdapter.setBounds(bounds);
            }
        });

        CameraUpdate center = CameraUpdateFactory.newLatLng(kediriPos);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        gMap.moveCamera(center);
        gMap.animateCamera(zoom);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locNETWORKListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locGPSListener);

        etOrigin.setAdapter(mAutoAdapter);
        etDestination.setAdapter(mAutoAdapter);

        etOrigin.setOnItemClickListener(originOnItemClick);
        etDestination.setOnItemClickListener(destOnItemClick);

        etOrigin.addTextChangedListener(originListener);
        etDestination.addTextChangedListener(destListener);

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ReceipeActivity.class);
                startActivity(i);
            }
        });

        pickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // sendRequest();
            }
        });

        findPath.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });


    }

    private View.OnClickListener btnSubmitOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.hide();
        }
    };

    private LocationListener locNETWORKListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

            gMap.moveCamera(center);
            gMap.animateCamera(zoom);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private LocationListener locGPSListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

            gMap.moveCamera(center);
            gMap.animateCamera(zoom);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void route() {
        if (start == null || end == null) {
            if (start == null) {
                if (etOrigin.getText().length() > 0) {
                    etOrigin.setError("Choose location from dropdown.");
                } else {
                    Toast.makeText(this, "Please choose a starting point.", Toast.LENGTH_SHORT).show();
                }
            }
            if (end == null) {
                if (etDestination.getText().length() > 0) {
                    etDestination.setError("Choose location from dropdown.");
                } else {
                    Toast.makeText(this, "Please choose a destination.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            progressDialog = ProgressDialog.show(this, "Silakan Tunggu.",
                    "Sedang mengambil informasi rute.", true);
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(start, end)
                    .units("metric")
                    .build();
            routing.execute();
        }
    }

    private TextWatcher destListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (end != null) {
                end = null;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher originListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int startNum, int before, int count) {
            if (start != null) {
                start = null;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private AdapterView.OnItemClickListener destOnItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAutoAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(PlaceBuffer places) {
                    if (!places.getStatus().isSuccess()) {
                        // Request did not complete successfully
                        Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                        places.release();
                        return;
                    }
                    // Get the Place object from the buffer.
                    final Place place = places.get(0);

                    end = place.getLatLng();
                }
            });
        }
    };

    private AdapterView.OnItemClickListener originOnItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAutoAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(PlaceBuffer places) {
                    if (!places.getStatus().isSuccess()) {
                        // Request did not complete successfully
                        Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                        places.release();
                        return;
                    }
                    // Get the Place object from the buffer.
                    final Place place = places.get(0);

                    start = place.getLatLng();

                    etDestination.requestFocus();
                }
            });
        }
    };


    public void sendRequest() {
        if (Util.Operations.isOnline(this)) {
            route();
        } else {
            Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onRoutingFailure(RouteException e) {
        progressDialog.dismiss();
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(List<Route> routes, int shortestRouteIndex) {
        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        gMap.moveCamera(center);


        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }


        polylines = new ArrayList<Polyline>();
        //add route(s) to the map.

        Route route = routes.get(shortestRouteIndex);
        if (route != null) {
            int colorIndex = 0;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10);
            polyOptions.addAll(route.getPoints());
            Polyline polyline = gMap.addPolyline(polyOptions);
            polylines.add(polyline);

            double distKm = ((double) route.getDistanceValue()) / 1000;
            int dur = route.getDurationValue();
            String hargaTotal = CalcHelper.getFormattedHargaTotal(drivingMode, route.getDistanceValue(), route.getDurationValue());

            String sDistKm = CalcHelper.formatAngka(distKm);

            tvDistance.setText(sDistKm+" km");
            tvDuration.setText(CalcHelper.formatWaktu(dur));

            lblJarak.setText(sDistKm + " km");
            lblTotal.setText(hargaTotal);
            dialog.show();


        }


        gMap.addMarker(new MarkerOptions()
                .position(start)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
        );

        gMap.addMarker(new MarkerOptions()
                .position(end)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
        );


    }

    @Override
    public void onRoutingCancelled() {

    }
}
