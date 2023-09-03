package pdf;



import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DashedBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;

import java.io.FileNotFoundException;
import java.util.Optional;


import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import dao.AccountDao;
import dao.BankDao;
import entity.Account;
import entity.Bank;
import entity.Transaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

/**
 * The type Receipt pdf service.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReceiptPdfServiceImpl implements ReceiptPdfService {


    @Override
    @SneakyThrows
    public void generatePdf(Transaction transaction,Document document) {
        final BankDao bankDao = BankDao.getInstance();
        final AccountDao accountDao = AccountDao.getInstance();

        Optional<Account> senderAccountOptional = accountDao.get(transaction.getSenderAccount());
        Account senderAccount = senderAccountOptional.get();

        Optional<Account> receiverAccountOptional = accountDao.get(transaction.getReceiverAccount());
        Account receiverAccount = new Account();
        if (receiverAccountOptional.isPresent()) {
            receiverAccount = receiverAccountOptional.get();
        }

        Optional<Bank> senderBankOptional = bankDao.get(senderAccount.getBankId());
        Bank senderBank = senderBankOptional.get();

        Optional<Bank> receiverBankOptional = Optional.of(new Bank());
        if (receiverAccountOptional.isPresent()){
            receiverBankOptional =  bankDao.get(receiverAccount.getBankId());
        }

        Bank receiverBank = new Bank();

        if (receiverBankOptional.isPresent()){
            receiverBank = receiverBankOptional.get();
        }

        Table table = new Table(2);
        table.setMarginTop(20);
        table.setBorder(new DashedBorder(1));
        table.setWidth(250);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addCell(new Cell(1, 2).add(new Paragraph("Banking receipt")).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1,1)
                .add(new Paragraph("Receipt:")).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1,1)
                .add(new Paragraph(String.valueOf(transaction.getId()))).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1,1)
                .add(new Paragraph(transaction.getDateOfTransaction())).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1,1)
                .add(new Paragraph(transaction.getTimeOfTransaction())).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1,1)
                .add(new Paragraph("Transaction type: ")).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1,1)
                .add(new Paragraph(transaction.getTransactionType())).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

        String buff = transaction.getTransactionType();

        switch (buff) {
            case "TRANSFER" -> {
                table.addCell(new Cell(1, 1)
                        .add(new Paragraph("Sender's bank: ")).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
                table.addCell(new Cell(1, 1)
                        .add(new Paragraph(senderBank.getBankName())).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
                table.addCell(new Cell(1, 1)
                        .add(new Paragraph("Receiver's bank: ")).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
                table.addCell(new Cell(1, 1)
                        .add(new Paragraph(receiverBank.getBankName())).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

                table.addCell(new Cell(1, 1)
                        .add(new Paragraph("Sender's account: ")).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
                table.addCell(new Cell(1, 1)
                        .add(new Paragraph(String.valueOf(transaction.getSenderAccount()))).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
                table.addCell(new Cell(1, 1)
                        .add(new Paragraph("Receiver's account: ")).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
                table.addCell(new Cell(1, 1)
                        .add(new Paragraph(String.valueOf(transaction.getReceiverAccount()))).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
            }
            case "DEPOSIT", "WITHDRAW" -> {
                table.addCell(new Cell(1, 1)
                        .add(new Paragraph("Bank: ")).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
                table.addCell(new Cell(1, 1)
                        .add(new Paragraph(senderBank.getBankName())).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
                table.addCell(new Cell(1, 1)
                        .add(new Paragraph("Account: ")).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
                table.addCell(new Cell(1, 1)
                        .add(new Paragraph(String.valueOf(transaction.getSenderAccount()))).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
            }
            default -> {
            }
        }
            table.addCell(new Cell(1,1)
                .add(new Paragraph("Amount: ")).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            table.addCell(new Cell(1,1)
                .add(new Paragraph(transaction.getAmount() + " " + "BYN")).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

        document.add(table);
        document.close();
    }

    @Override
    public Document createDocument(String dest) throws FileNotFoundException {
        PdfWriter pdfWriter = new PdfWriter(dest);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);
        return document;
    }


}
