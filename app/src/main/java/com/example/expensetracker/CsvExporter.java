package com.example.expensetracker;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class CsvExporter {

    public static File exportCategoryReport(Context context, List<CategoryTotal> data) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "category_report.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Category,Total\n");
            for (CategoryTotal ct : data) {
                writer.append(ct.category).append(",").append(String.valueOf(ct.total)).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File exportMonthlyReport(Context context, List<MonthTotal> data) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "monthly_report.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Month,Total\n");
            for (MonthTotal mt : data) {
                writer.append(mt.month).append(",").append(String.valueOf(mt.total)).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
