package pdf;

import com.itextpdf.layout.Document;
import entity.Account;
import entity.Transaction;

import java.io.FileNotFoundException;

/**
 * The interface Receipt pdf service.
 */
public interface ReceiptPdfService {
    /**
     * Generate pdf.
     *
     * @param transaction the transaction
     * @param document    the document
     */
    void generatePdf(Transaction transaction, Document document);

    /**
     * Create document document.
     *
     * @param dest the dest
     * @return the document
     * @throws FileNotFoundException the file not found exception
     */
    Document createDocument(String dest) throws FileNotFoundException;
}
