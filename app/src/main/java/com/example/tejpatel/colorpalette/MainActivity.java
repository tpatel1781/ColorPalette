package com.example.tejpatel.colorpalette;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.R.attr.bitmap;
import static android.R.attr.color;
import static android.R.color.black;

public class MainActivity extends AppCompatActivity {

    // Request code for opening gallery
    public static final int IMAGE_REQUEST_CODE = 20;

    // Request code for opening camera
    public  static final int CAMERA_REQUEST = 40;

    // Instantiating hexcode textviews
    private TextView vibrant;
    private TextView vibrantDark;
    private TextView vibrantLight;
    private TextView muted;
    private TextView mutedDark;
    private TextView mutedLight;

    private ImageView photo;

    private FloatingActionMenu fabMenu;
    private FloatingActionButton chooseImageFromGalleryButton;
    private FloatingActionButton cameraButton;

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

        fabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);

        chooseImageFromGalleryButton = (FloatingActionButton) findViewById(R.id.choose_from_gallery_button);
        cameraButton = (FloatingActionButton) findViewById(R.id.camera_button);

        chooseImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseFromGalleryButtonClicked(chooseImageFromGalleryButton);

                // Close menu after pressing gallery button
                fabMenu.close(true);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraButtonClicked(cameraButton);

                // Close menu after pressing camera button
                fabMenu.close(true);
            }
        });
    }

    // Method to choose an existing image from the gallery
    public void onChooseFromGalleryButtonClicked(View v) {
        // Invoke the image gallery with implicit intent
        Intent picturePickerIntent = new Intent(Intent.ACTION_PICK);

        // Where to get file from
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        // Get URI representation of string path
        Uri data = Uri.parse(pictureDirectoryPath);

        // Set data and type to look for. Set to look for all image types: jpeg, jpg, png, gif
        picturePickerIntent.setDataAndType(data, "image/*");

        // Pass intent and request code data to onActivityResult() method
        startActivityForResult(picturePickerIntent, IMAGE_REQUEST_CODE);
    }

    // Method to open camera activity to take new picture
    public void onCameraButtonClicked(View v) {
        // Intent to open camera activity
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        // Pass intent and request code data to onActivityResult() method
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            // If we pass this if statement, everything was processed successfully
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

                // After setting selected image from gallery to picture in ImageView, generate palette
                createPalette();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                // Show toast to user if any exception is caught
                Toast.makeText(this, "Unable to open image", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            // If we pass this statement, everything was processed successfully
            // At this point we are hearing from the camera
            Bitmap image = (Bitmap) data.getExtras().get("data");

            // Set ImageView photo to picture taken from camera
            photo.setImageBitmap(image);

            // After setting the taken photo as picture to show in ImageView, create palette
            createPalette();
        }
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

                    /* If/else statement for vibrant swatch to handle possible null pointer exception
                       if there is no vibrant color found
                    / If swatch cannot be found, change text to "Vibrant swatch not found" */
                    if (palette.getVibrantSwatch() != null) {
                        String vibrantHexcode = "#" + Integer.toHexString(palette.getVibrantSwatch().getRgb()).toUpperCase().substring(2, 8);
                        setViewSwatch(vibrant, palette.getVibrantSwatch(), "Vibrant: " + vibrantHexcode);

                        // Change ActionBar color to vibrant color
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(vibrantHexcode)));

                        // Change fabMenu button colors to vibrant color
                        setFloatingActionButtonColor(vibrantHexcode);
                    } else {
                        setNullViewSwatch(vibrant, "Vibrant swatch not found");
                    }

                    // If/else statement for light vibrant swatch to handle pointer exception
                    if (palette.getLightVibrantSwatch() != null) {
                        String lightVibrantHexcode = "#" + Integer.toHexString(palette.getLightVibrantSwatch().getRgb()).toUpperCase().substring(2, 8);
                        setViewSwatch(vibrantLight, palette.getLightVibrantSwatch(), "Light Vibrant: " + lightVibrantHexcode);

                        // If there is no vibrant swatch then change ActionBar color to light vibrant
                        if (palette.getVibrantSwatch() == null) {
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(lightVibrantHexcode)));

                            // Change fabMenu button color to light vibrant color
                            setFloatingActionButtonColor(lightVibrantHexcode);
                        }

                    } else {
                        setNullViewSwatch(vibrantLight, "Light vibrant swatch not found");
                    }

                    // If/else statement for dark vibrant swatch to handle possible null pointer exception
                    if (palette.getDarkVibrantSwatch() != null) {
                        String darkVibrantHexcode = "#" + Integer.toHexString(palette.getDarkVibrantSwatch().getRgb()).toUpperCase().substring(2, 8);
                        setViewSwatch(vibrantDark, palette.getDarkVibrantSwatch(), "Dark Vibrant: " + darkVibrantHexcode);

                        // If there is a dark muted palette color then set the status bar color to it
                        getWindow().setStatusBarColor(Color.parseColor(darkVibrantHexcode));

                        // If there is no vibrant or light vibrant swatch then change ActionBar color to dark vibrant
                        if (palette.getVibrantSwatch() == null && palette.getLightVibrantSwatch() == null) {
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(darkVibrantHexcode)));

                            // Change fabMenu button color to dark vibrant
                            setFloatingActionButtonColor(darkVibrantHexcode);
                        }

                    } else {
                        setNullViewSwatch(vibrantDark, "Dark vibrant swatch not found");
                    }

                    // If/else statement for muted swatch to handle possible null pointer exception
                    if (palette.getMutedSwatch() != null) {
                        String mutedHexcode = "#" + Integer.toHexString(palette.getMutedSwatch().getRgb()).toUpperCase().substring(2, 8);
                        setViewSwatch(muted, palette.getMutedSwatch(), "Muted: " + mutedHexcode);

                        // If a muted color exists, set the ImageView background to the muted color
                        photo.setBackgroundColor(Color.parseColor(mutedHexcode));

                        // If there is no vibrant, light vibrant, or dark vibrant swatch then change ActionBar color to muted
                        if (palette.getVibrantSwatch() == null && palette.getLightVibrantSwatch() == null && palette.getDarkVibrantSwatch() == null) {
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mutedHexcode)));

                            // Change fabMenu button color to muted
                            setFloatingActionButtonColor(mutedHexcode);
                        }

                    } else {
                        setNullViewSwatch(muted, "Muted swatch not found");
                    }

                    // If/else statement for light muted swatch to handle possible null pointer exception
                    if (palette.getLightMutedSwatch() != null) {
                        String lightMutedHexcode = "#" + Integer.toHexString(palette.getLightMutedSwatch().getRgb()).toUpperCase().substring(2, 8);
                        setViewSwatch(mutedLight, palette.getLightMutedSwatch(), "Light Muted: " + lightMutedHexcode);

                        // If there is no vibrant, light/dark vibrant, or muted swatch then change ActionBar color to light muted
                        if (palette.getVibrantSwatch() == null && palette.getLightVibrantSwatch() == null && palette.getDarkVibrantSwatch() == null && palette.getMutedSwatch() == null) {
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(lightMutedHexcode)));

                            // Set fabMenu button color to light muted
                            setFloatingActionButtonColor(lightMutedHexcode);
                        }

                        // If there is no muted color, set the ImageView background color to light muted
                        if (palette.getMutedSwatch() == null) {
                            photo.setBackgroundColor(Color.parseColor(lightMutedHexcode));
                        }

                    } else {
                        setNullViewSwatch(mutedLight, "Light muted swatch not found");
                    }

                    // If/else statement for dark muted swatch to handle possible null pointer exception
                    if (palette.getDarkMutedSwatch() != null) {
                        String darkMutedHexcode = "#" + Integer.toHexString(palette.getDarkMutedSwatch().getRgb()).toUpperCase().substring(2, 8);
                        setViewSwatch(mutedDark, palette.getDarkMutedSwatch(), "Muted Dark: " + darkMutedHexcode);

                        // If there is no vibrant, light/dark vibrant, muted, or light muted swatch then change ActionBar color to dark muted
                        if (palette.getVibrantSwatch() == null && palette.getLightVibrantSwatch() == null && palette.getDarkVibrantSwatch() == null && palette.getMutedSwatch() == null && palette.getLightMutedSwatch() == null) {
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(darkMutedHexcode)));

                            // Set fabMenu button color to dark muted
                            setFloatingActionButtonColor(darkMutedHexcode);
                        }

                        // If there is no muted or light muted color, set the ImageView background color to dark muted
                        if (palette.getMutedSwatch() == null && palette.getLightMutedSwatch() == null) {
                            photo.setBackgroundColor(Color.parseColor(darkMutedHexcode));
                        }

                    } else {
                        setNullViewSwatch(mutedDark, "Dark muted swatch not found");

                        // If there are no colors in the palette at all, set ActionBar to black
                        // This should never happen, as there should always be at least one color in the palette
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
                    }
                }
            });
        }
    }

    // Method for setting color of the FloatingActionButtons and Menu, given a hexcode as a String
    private void setFloatingActionButtonColor(String colorHexcode) {
        // Set FloatingActionMenu button color to specified color
        fabMenu.setMenuButtonColorNormal(Color.parseColor(colorHexcode));
        fabMenu.setMenuButtonColorPressed(Color.parseColor(colorHexcode));

        // Set gallery FloatingActionButton to specified color
        chooseImageFromGalleryButton.setColorNormal(Color.parseColor(colorHexcode));
        chooseImageFromGalleryButton.setColorPressed(Color.parseColor(colorHexcode));

        // Set camera FloatingActionButton to specified color
        cameraButton.setColorNormal(Color.parseColor(colorHexcode));
        cameraButton.setColorPressed(Color.parseColor(colorHexcode));
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
}