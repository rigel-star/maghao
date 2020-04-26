package com.service.maghao.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import java.util.Arrays;
import java.util.List;

public class GoogleMapsPlacesAutoComplete {

    public static final int PLACE_PICKER_CODE = 1001;
    private Context context;
    private EditText editText;

    //will store place info
    private List<Place.Field> fields;

    public GoogleMapsPlacesAutoComplete(Context context, EditText editText){
        this.context = context;
        this.editText = editText;

        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        //init places var with G-map api key of this app
        Places.initialize(context, "AIzaSyAyuWzP9o8rlHqZf2j2C6MCkyvY3ODvjtI");
        //places client instance
        PlacesClient placesClient = Places.createClient(context);

        Activity activity = (Activity) context;

        editText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(context);
                        activity.startActivityForResult(intent, PLACE_PICKER_CODE);
                    }
                }
        );

    }


}
