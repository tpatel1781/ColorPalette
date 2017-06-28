package com.example.tejpatel.colorpalette;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.R.attr.bitmap;
import static android.R.color.black;

public class MainActivity extends AppCompatActivity {

    public static final int IMAGE_REQUEST_CODE = 20;
    // Instantiating hexcode textviews
    private TextView vibrant;
    private TextView vibrantDark;
    private TextView vibrantLight;
    private TextView muted;
    private TextView mutedDark;
    private TextView mutedLight;

    private ImageView photo;

    private Button chooseImageFromGalleryButton;
    private Button createPaletteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrant = (TextView) findViewById(R.id.vibrant_text_view);
        vibrantLight = (TextView) findViewById(R.id.vibrant_light_text_view);
        vibrantDark = (TextView) findViewById(R.id.vibrant_dark_text_view);
        muted = (TextView) findViewById(R.id.muted_text_view);
        mutedLight = (TextView) findViewById(R.id.muted_light_text_view);
        mutedDark = (TextView) findViewById(R.id.muted_dark_text_view);
        photo = (ImageView) findViewById(R.id.photo);
        chooseImageFromGalleryButton = (Button) findViewById(R.id.choose_from_gallery_button);
        createPaletteButton = (Button) findViewById(R.id.create_palette_button);


        chooseImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseImageButtonClicked(chooseImageFromGalleryButton);
            }
        });

        createPaletteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPalette();
            }
        });
    }

    private void createPalette() {
        // Cast the photo in the ImageView as a BitmapDrawable and get bitmap from it
        Bitmap bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();

        // If the bitmap is not empty, asynchronously create a palette
        if (bitmap != null) {
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                // Override the onGenerated method. Takes the generated palette as a parameter and
                // passes the palette and view ID and text to the setViewSwatch method
                @Override
                public void onGenerated(Palette palette) {
                    // If/else statement for each swatch to handle possible null pointer exception
                    // If swatch cannot be found, change text to "Vibrant swatch not found"
                    if (palette.getVibrantSwatch() != null) {
                        setViewSwatch(vibrant, palette.getVibrantSwatch(), "Vibrant: #" + Integer.toHexString(palette.getVibrantSwatch().getRgb() & 0x00ffffff).toUpperCase());
                    } else {
                        setNullViewSwatch(vibrant, "Vibrant swatch not found");
                    }

                    if (palette.getLightVibrantSwatch() != null) {
                        setViewSwatch(vibrantLight, palette.getLightVibrantSwatch(), "Light Vibrant: #" + Integer.toHexString(palette.getLightVibrantSwatch().getRgb() & 0x00ffffff).toUpperCase());
                    } else {
                        setNullViewSwatch(vibrantLight, "Light vibrant swatch not found");
                    }

                    if (palette.getDarkVibrantSwatch() != null) {
                        setViewSwatch(vibrantDark, palette.getDarkVibrantSwatch(), "Dark Vibrant: #" + Integer.toHexString(palette.getDarkVibrantSwatch().getRgb() & 0x00ffffff).toUpperCase());
                    } else {
                        setNullViewSwatch(vibrantDark, "Dark vibrant swatch not found");
                    }
                    if (palette.getMutedSwatch() != null) {
                        setViewSwatch(muted, palette.getMutedSwatch(), "Muted: #" + Integer.toHexString(palette.getMutedSwatch().getRgb() & 0x00ffffff).toUpperCase());
                    } else {
                        setNullViewSwatch(muted, "Muted swatch not found");
                    }

                    if (palette.getLightMutedSwatch() != null) {
                        setViewSwatch(mutedLight, palette.getLightMutedSwatch(), "Light Muted: #" + Integer.toHexString(palette.getLightMutedSwatch().getRgb() & 0x00ffffff).toUpperCase());
                    } else {
                        setNullViewSwatch(mutedLight, "Light muted swatch not found");
                    }

                    if (palette.getDarkMutedSwatch() != null) {
                        setViewSwatch(mutedDark, palette.getDarkMutedSwatch(), "Muted Dark: #" + Integer.toHexString(palette.getDarkMutedSwatch().getRgb() & 0x00ffffff).toUpperCase());
                    } else {
                        setNullViewSwatch(mutedDark, "Dark muted swatch not found");
                    }
                }
            });
        }
    }

    // Method for getting swatch for palette and assigning the color to the corresponding textview
    private void setViewSwatch(TextView view, Palette.Swatch swatch, final String title) {
        if (swatch != null) {
            // Set the background color of the textview based on the vibrant color
            view.setBackgroundColor(swatch.getRgb());
            // Set text of the textview to match the name for each color in the palette
            view.setText(title);
        }
    }

    // Method for setting textview background to black and printing out swatch not found message
    // to user if getSwatch() method returns null
    private void setNullViewSwatch(TextView view, final String title) {
        // Set textview background color to black
        view.setBackgroundResource(R.color.black);
        // Set textview text
        view.setText(title);
    }

    public void onChooseImageButtonClicked(View v) {
        // Invoke the image gallery with implicit intent
        Intent picturePickerIntent = new Intent(Intent.ACTION_PICK);

        // Where to get file from
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        // Get URI representation of string path
        Uri data = Uri.parse(pictureDirectoryPath);

        // Set data and type to look for. Set to look for all image types: jpeg, jpg, png, gif
        picturePickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(picturePickerIntent, IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // If we pass this if statement, everything was processed successfully
            if (requestCode == IMAGE_REQUEST_CODE) {
                // If we are here, we are hearing from the image gallery

                // The address of the image on the SD card
                Uri imageUri = data.getData();

                // Declare a stream to read the image data from the SD card
                InputStream inputStream;

                // Here we are getting an input stream based on the URI of the image
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    // Gets bitmap from the stream
                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    // Show image from SD card in the ImageView
                    photo.setImageBitmap(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // Show toast to user if any exception is caught
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
