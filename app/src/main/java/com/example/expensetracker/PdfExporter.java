package com.example.expensetracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfExporter {

    public static File exportReport(Context context, List<CategoryTotal> categories, List<MonthTotal> months) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "expense_report.pdf");

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int x = 40, y = 60;

        // Title
        paint.setTextSize(20);
        paint.setFakeBoldText(true);
        canvas.drawText("Expense Tracker Report", x, y, paint);

        y += 40;
        paint.setTextSize(14);
        paint.setFakeBoldText(false);

        // Category Section
        canvas.drawText("Category-wise Spending:", x, y, paint);
        y += 30;

        for (CategoryTotal ct : categories) {
            canvas.drawText(ct.category + " : ₹" + ct.total, x + 20, y, paint);
            y += 20;
        }

        y += 30;
        canvas.drawText("Monthly Spending:", x, y, paint);
        y += 30;

        for (MonthTotal mt : months) {
            canvas.drawText(mt.month + " : ₹" + mt.total, x + 20, y, paint);
            y += 20;
        }

        pdfDocument.finishPage(page);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();
        return file;
    }
}
