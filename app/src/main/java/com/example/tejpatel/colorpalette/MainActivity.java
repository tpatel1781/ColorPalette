package com.example.tejpatel.colorpalette;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;
import android.widget.TextView;

import static android.R.attr.bitmap;

public class MainActivity extends AppCompatActivity {

    // Instantiating hexcode textviews
    private TextView vibrant;
    private TextView vibrantDark;
    private TextView vibrantLight;
    private TextView muted;
    private TextView mutedDark;
    private TextView mutedLight;

    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrant = (TextView) findViewById(R.id.vibrant_text_view);
        vibrantDark = (TextView) findViewById(R.id.vibrant_dark_text_view);
        vibrantLight = (TextView) findViewById(R.id.vibrant_light_text_view);
        muted = (TextView) findViewById(R.id.muted_text_view);
        mutedDark = (TextView) findViewById(R.id.muted_dark_text_view);
        mutedLight = (TextView) findViewById(R.id.muted_light_text_view);

        photo = (ImageView) findViewById(R.id.photo);

        Bitmap bitmap = ((BitmapDrawable)photo.getDrawable()).getBitmap();;

        if (bitmap != null) {
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    setViewSwatch(vibrant, palette.getVibrantSwatch(), "Vibrant");
                    setViewSwatch(vibrantDark, palette.getDarkVibrantSwatch(), "Dark Vibrant");
                    setViewSwatch(vibrantLight, palette.getLightVibrantSwatch(), "Light Vibrant");
                    setViewSwatch(muted, palette.getMutedSwatch(), "Muted");
                    setViewSwatch(mutedDark, palette.getDarkMutedSwatch(), "Dark Muted");
                    setViewSwatch(mutedLight, palette.getLightMutedSwatch(), "Light Muted");
                }
            });
        }
    }

    private void setViewSwatch(TextView view, Palette.Swatch swatch, final String title) {
        if (swatch != null) {
            // Set the background color of the textview based on the vibrant color
            view.setBackgroundColor(swatch.getRgb());
            view.setText(title);
        }
    }

}
