package services;

import beans.Etudiant;
import beans.Note;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import exceptions.ServiceException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportService {
    
    private NoteService noteService;
    
    public ExportService() {
        this.noteService = new NoteService();
    }
    
    /**
     * exporter les notes d'un étudiant en CSV
     */
    public void exporterNotesCSV(int idEtudiant, HttpServletResponse response) throws ServiceException, IOException {
        List<Note> notes = noteService.getNotesByEtudiant(idEtudiant);
        
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", 
            "attachment; filename=notes_etudiant_" + idEtudiant + ".csv");
        
        try (PrintWriter writer = response.getWriter()) {
            writer.println("Module;Coefficient;Note;Session;Date");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            double sommeCoeff = 0;
            double sommeNotes = 0;
            
            for (Note n : notes) {
                writer.println(
                    n.getModule().getNom() + ";" +
                    n.getModule().getCoefficient() + ";" +
                    n.getNote() + ";" +
                    n.getSession() + ";" +
                    (n.getDateSaisie() != null ? n.getDateSaisie().format(formatter) : "")
                );
                
                sommeCoeff += n.getModule().getCoefficient();
                sommeNotes += n.getNote() * n.getModule().getCoefficient();
            }
            
            double moyenne = sommeCoeff > 0 ? sommeNotes / sommeCoeff : 0;
            writer.println();
            writer.println("Moyenne Générale;;" + String.format("%.2f", moyenne) + ";;");
        }
    }
    
    /**
     * exporter le bulletin d'un étudiant en PDF
     */
    public void exporterBulletinPDF(Etudiant etudiant, List<Note> notes, HttpServletResponse response) 
            throws Exception {
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", 
            "attachment; filename=bulletin_" + etudiant.getCne() + ".pdf");
        
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        
        document.open();
        
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
        
        // en-tête avec deux colonnes
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{60, 40});

        PdfPCell cellLeft = new PdfPCell(new Phrase("UNIVERSITE MOHAMED PREMIER - OUJDA", titleFont));
        cellLeft.setBorder(Rectangle.NO_BORDER);
        cellLeft.setHorizontalAlignment(Element.ALIGN_LEFT);
        cellLeft.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellLeft.setPaddingBottom(10);
        headerTable.addCell(cellLeft);

        PdfPCell cellRight = new PdfPCell(new Phrase("FACULTE DES SCIENCES", titleFont));
        cellRight.setBorder(Rectangle.NO_BORDER);
        cellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellRight.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellRight.setPaddingBottom(10);
        headerTable.addCell(cellRight);

        document.add(headerTable);
        
        Paragraph subtitle = new Paragraph("BULLETIN DE NOTES", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);
        
        Paragraph annee = new Paragraph("Année Universitaire 2025-2026", normalFont);
        annee.setAlignment(Element.ALIGN_CENTER);
        document.add(annee);
        
        document.add(Chunk.NEWLINE);
        
        // informations étudiant
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10f);
        infoTable.setSpacingAfter(10f);
        
        PdfPCell infoCell = new PdfPCell(new Phrase("Nom:", boldFont));
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(infoCell);
        infoCell = new PdfPCell(new Phrase(etudiant.getNom()));
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(infoCell);
        
        infoCell = new PdfPCell(new Phrase("Prénom:", boldFont));
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(infoCell);
        infoCell = new PdfPCell(new Phrase(etudiant.getPrenom()));
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(infoCell);
        
        infoCell = new PdfPCell(new Phrase("CNE:", boldFont));
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(infoCell);
        infoCell = new PdfPCell(new Phrase(etudiant.getCne()));
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(infoCell);
        
        infoCell = new PdfPCell(new Phrase("Classe:", boldFont));
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(infoCell);
        infoCell = new PdfPCell(new Phrase(etudiant.getClasse() != null ? etudiant.getClasse() : ""));
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(infoCell);
        
        infoCell = new PdfPCell(new Phrase("Date:", boldFont));
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(infoCell);
        infoCell = new PdfPCell(new Phrase(java.time.LocalDate.now().toString()));
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(infoCell);
        
        document.add(infoTable);
        document.add(Chunk.NEWLINE);
        
        // tableau des notes
        if (notes != null && !notes.isEmpty()) {
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 1, 1, 1, 2});
            
            PdfPCell cell = new PdfPCell(new Phrase("Module", headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Coeff.", headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Note", headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Session", headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Appréciation", headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
            
            double sommeCoeff = 0;
            double sommeNotes = 0;
            
            for (Note n : notes) {
                table.addCell(n.getModule().getNom());
                table.addCell(String.valueOf(n.getModule().getCoefficient()));
                
                PdfPCell noteCell = new PdfPCell(new Phrase(String.format("%.2f", n.getNote())));
                noteCell.setBackgroundColor(n.getNote() >= 10 ? BaseColor.GREEN : BaseColor.RED);
                noteCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                noteCell.setPadding(5);
                table.addCell(noteCell);
                
                table.addCell(n.getSession());
                
                String appreciation;
                if (n.getNote() >= 16) appreciation = "Très Bien";
                else if (n.getNote() >= 14) appreciation = "Bien";
                else if (n.getNote() >= 12) appreciation = "Assez Bien";
                else if (n.getNote() >= 10) appreciation = "Passable";
                else appreciation = "Insuffisant";
                
                table.addCell(appreciation);
                
                sommeCoeff += n.getModule().getCoefficient();
                sommeNotes += n.getNote() * n.getModule().getCoefficient();
            }
            
            document.add(table);
            
            document.add(Chunk.NEWLINE);
            double moyenne = sommeCoeff > 0 ? sommeNotes / sommeCoeff : 0;
            
            PdfPTable moyTable = new PdfPTable(2);
            moyTable.setWidthPercentage(50);
            moyTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            
            PdfPCell moyCell = new PdfPCell(new Phrase("Moyenne Générale:", boldFont));
            moyCell.setBorder(Rectangle.NO_BORDER);
            moyCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            moyTable.addCell(moyCell);
            
            moyCell = new PdfPCell(new Phrase(String.format("%.2f / 20", moyenne), boldFont));
            moyCell.setBorder(Rectangle.NO_BORDER);
            moyCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            moyTable.addCell(moyCell);
            
            document.add(moyTable);
            
            String mention;
            if (moyenne >= 16) mention = "TRÈS BIEN";
            else if (moyenne >= 14) mention = "BIEN";
            else if (moyenne >= 12) mention = "ASSEZ BIEN";
            else if (moyenne >= 10) mention = "PASSABLE";
            else mention = "INSUFFISANT";
            
            Paragraph mentionPara = new Paragraph("Mention: " + mention, boldFont);
            mentionPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(mentionPara);
            
        } else {
            Paragraph noNotes = new Paragraph("Aucune note disponible pour cet étudiant.", normalFont);
            noNotes.setAlignment(Element.ALIGN_CENTER);
            document.add(noNotes);
        }
        
        // signatures
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        
        PdfPTable signTable = new PdfPTable(2);
        signTable.setWidthPercentage(100);
        
        PdfPCell signLeft = new PdfPCell(new Phrase("Fait à Oujda, le " + java.time.LocalDate.now(), normalFont));
        signLeft.setBorder(Rectangle.NO_BORDER);
        signTable.addCell(signLeft);
        
        PdfPCell signRight = new PdfPCell(new Phrase("Le Chef de département Informatique", normalFont));
        signRight.setBorder(Rectangle.NO_BORDER);
        signRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
        signTable.addCell(signRight);
        
        document.add(signTable);
        document.close();
    }
    
    /**
     * exporter les notes d'une matière en CSV (pour les professeurs)
     */
    public void exporterNotesMatiereCSV(int idMatiere, HttpServletResponse response) throws ServiceException, IOException {
        List<Note> notes = noteService.getNotesByMatiere(idMatiere);
        
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", 
            "attachment; filename=notes_matiere_" + idMatiere + ".csv");
        
        try (PrintWriter writer = response.getWriter()) {
            writer.println("CNE;Nom;Prénom;Note;Session;Date");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Note n : notes) {
                writer.println(
                    n.getEtudiant().getCne() + ";" +
                    n.getEtudiant().getNom() + ";" +
                    n.getEtudiant().getPrenom() + ";" +
                    n.getNote() + ";" +
                    n.getSession() + ";" +
                    (n.getDateSaisie() != null ? n.getDateSaisie().format(formatter) : "")
                );
            }
        }
    }
}