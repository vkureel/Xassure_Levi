package com.xassure.utilities;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PDFReader {
    public String readPdf(String filePath) {
        PDFTextStripper pdfStripper = null;
        String parsedText = null;
        PDDocument pdDoc = null;
        File file = new File(filePath);

        try {
            pdfStripper = new PDFTextStripper();
            pdDoc = PDDocument.load(file);
            parsedText = pdfStripper.getText(pdDoc);
            System.out.println(parsedText);
        } catch (IOException e) {

            e.printStackTrace();
        }
        return parsedText;
    }
}
