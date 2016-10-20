package com.bumblebeem.android.bumblebeem;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by n3v10t on 02/10/16.
 */

public class Read {
    private ArrayList<Word> contents;
    private String filename = "grade1";

    public Read(Context context, String filename, int levelInt ) {
        this.filename = filename;
        try {
            contents = new ArrayList<Word>();
            InputStream in = context.getResources().openRawResource(
                    context.getResources().getIdentifier(filename,
                            "raw", context.getPackageName()));
            //context.getAssets().open(filename);

            if (in != null) {
                // prepare the file for reading
                InputStreamReader input = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(input);
                String line = br.readLine();

                while ((line = br.readLine()) != null) {

                    // checks if line is empty if not safes in ArrayList

                    // Removes all possible occurrence of asterisks
                    if(line.endsWith("*"))
                    {
                        line = line.substring(0, line.length()-1);
                    }
                    contents.add(new Word(line, null, levelInt));
                }
                in.close();
            } else {
                System.out.println("It's the assests");
            }

        } catch (IOException e) {
            System.out.println("Couldn't Read File Correctly");
        }
    }

    public ArrayList<Word> loadFile() {
        return this.contents;
    }
}
