

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class ExportController {

	@Autowired
	private StudyRepository rpst_study;

	@Autowired
	private CRFRepository rpst_crf;

	@Autowired
	private DMRepository rpst_dm;

	@Autowired
	AuditRepository rpst_audit;

	@Autowired
	private UserRepository rpst_user;

	@Autowired
	private UserListRepository rpst_userlist;

	@Autowired
	private ParticipantRepository rpst_participant;

	final String IMG = "classpath:static/files/...jpg";
	final Paragraph SINGLE_SELECTION = new Paragraph().add(new Text("( )").setFontSize(10));
	final Paragraph MULTIPLE_SELECTION = new Paragraph().add(new Text("( )").setFontSize(10));
	final Paragraph SINGLE_SELECTION_CHECK = new Paragraph().add(new Text("(")).add(new Text("X").setFontColor(ColorConstants.BLUE).setBold()).add(new Text(")"));
	final Paragraph MULTIPLE_SELECTION_CHECK = new Paragraph().add(new Text("[")).add(new Text("X").setFontColor(ColorConstants.BLUE).setBold()).add(new Text("]"));

	protected PdfFormXObject template;
	protected Image total;
	protected PdfFont font_regular;

	class PDFEventHandler implements IEventHandler {
		protected PdfFont font_regular;
		protected String info;

		public void setInfo(String info) {
			this.info = info;
		}

		public String getInfo() {
			return info;
		}

		@Override
		public void handleEvent(Event event) {
			PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
			PdfPage page = docEvent.getPage();
			int pageNum = docEvent.getDocument().getPageNumber(page);
			PdfCanvas canvas = new PdfCanvas(page);
			canvas.beginText();
			try {
				canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 8);
			} catch (IOException e) {
				e.printStackTrace();
			}

			canvas.moveText(30, 20);
			canvas.showText(info);
			canvas.moveText(470, 0); // larger ==> right
			canvas.showText(String.format("Page %d of", pageNum));
			canvas.endText();
			canvas.stroke();
			canvas.addXObject(template, 0, 0);
			canvas.release();
		}
	}

	@PostMapping("/export_audit/{...}/{...}")
	public void buildAuditLog(@PathVariable String ..., HttpServletRequest ..., HttpServletResponse ..., HttpSession ...) {
		// create audit log and export it in PDF format.
	}

	@PostMapping("/export_ecrf/{...}/{...}")
	public void buildECrf(@PathVariable String ..., @PathVariable String ..., HttpServletRequest ..., HttpServletResponse ..., HttpSession ...) {
		// export multiple xlsx and zip out
	}

	private Object callGetter(Object obj, String fieldName, List<String> listE) {
		PropertyDescriptor pd;
		try {
			if (!listE.contains(fieldName))
				return null;

			pd = new PropertyDescriptor(fieldName, obj.getClass());
			return pd.getReadMethod().invoke(obj);
		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	private int filloutSheet4DM(Workbook wb, String studyid, String siteid, Double version) {
		String domain = "DM";

		List<CRF> listHead = rpst_crf.findAllByStudyidAndDomainAndVersionGroupByCdashig(...);
		if (listHead.size() == 0)
			return 0;

		List<String> listUniqueHead = listHead.stream().map(CRF::getCdashig).collect(Collectors.toList());

		XSSFSheet sheet = (XSSFSheet) wb.createSheet(domain);
		List<DM> listContent = rpst_dm.findAllBySTUDYIDAndSITEID(studyid, siteid);

		int index_endCol = listHead.size() + 2; // added visit and SCRNO
		int index_endRow = listContent.size();

		CellStyle headerCellStyle = drawTable(wb, index_endRow, index_endCol, sheet, domain);

		// initialize table header
		Row headerRow = sheet.createRow(0);
		for (int i = 0; i < index_endCol; i++) {
			org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
			cell.setCellStyle(headerCellStyle);
		}

		// Set the values for the table
		setCellValues(listUniqueHead, index_endRow, sheet, headerCellStyle, listContent, DM.class);

		return 1;
	}

	// ...

	private CellStyle drawTable(Workbook wb, int index_endRow, int index_endCol, XSSFSheet sheet, String domain) {
		// Set which area the table should be placed in
		AreaReference reference = wb.getCreationHelper().createAreaReference(new CellReference(0, 0), 
			new CellReference(index_endRow > 0 ? index_endRow : 1, index_endCol - 1)); // start, end - (row, col)

		// Create
		XSSFTable table = sheet.createTable(reference); // creates a table having 3 columns as of area reference
		// but all of those have id 1, so we need repairing

		for (int i = 1; i < index_endCol; i++) {
			table.getCTTable().getTableColumns().getTableColumnArray(i).setId(i + 1);
		}

		table.setName(domain);
		table.setDisplayName("Table_" + domain);

		CTAutoFilter autoFilter = sheet.getCTWorksheet().getAutoFilter();
		table.getCTTable().setAutoFilter(autoFilter);

		// For now, create the initial style in a low-level way
		table.getCTTable().addNewTableStyleInfo();
		table.getCTTable().getTableStyleInfo().setName("TableStyleMedium2");

		// Style the table
		XSSFTableStyleInfo style = (XSSFTableStyleInfo) table.getStyle();
		style.setName("TableStyleMedium2");
		style.setShowColumnStripes(false);
		style.setShowRowStripes(true);
		style.setFirstColumn(false);
		style.setLastColumn(false);

		// set cell width
		sheet.setDefaultColumnWidth(15);
		sheet.createFreezePane(0, 1);

		Font headerFont = wb.createFont();
		headerFont.setFontName("Calibri");
		headerFont.setFontHeightInPoints((short) 10);

		CellStyle headerCellStyle = wb.createCellStyle();
		headerCellStyle.setFillBackgroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

		return headerCellStyle;
	}

	private void setCellValues(List<String> listUniqueHead, int index_endRow, XSSFSheet sheet, CellStyle headerCellStyle, List<?> listObj, Class<?> className) {
		// Set the values for the table
		XSSFRow row;
		Field[] fields = className.getDeclaredFields();
		List<String> list = Arrays.stream(fields).filter(x -> !x.getName().equals("id") 
			&& !x.getName().contains("_comment") && listUniqueHead.contains(x.getName())).map(x -> x.getName()).collect(Collectors.toList());

		for (int i = 0; i < index_endRow; i++) {
			// Create row
			row = sheet.createRow(i + 1);

			int idx = 0;
			XSSFCell cell;
			for (String header : listUniqueHead) {
				cell = row.createCell(idx++);
				cell.setCellStyle(headerCellStyle);
				String result = callGetter(listObj.get(i), header, list) == null ? "" : callGetter(listObj.get(i), header, list).toString();
				cell.setCellValue(result);
			}
		}
	}

	@PostMapping("/export_annotatedpdf/{studyShortName}")
	public void buildAnnotatedPDF(@PathVariable String studyShortName, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) {

		try {
			List<Study> listStudy = rpst_study.findAllByShortname(studyShortName);
			String studyid = listStudy.get(0).getStudyid();
			Double version = listStudy.get(0).getVersion();

			String author = " ...";
			String outputFileName = "..._annotated.pdf";

			// list CRF DM
			List<CRF> listCRF = rpst_crf.findAllByStudyidAndVersion(studyid, version);
			if (listCRF.size() == 0) {
				// return
				return;
			}

			// write
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
			Document doc = new Document(pdfDoc, new PageSize(PageSize.A4), true);
			doc.setMargins(54, 36, 36, 36);

			template = new PdfFormXObject(new Rectangle(550, 20, 50, 50));
			PdfCanvas canvas = new PdfCanvas(template, pdfDoc);
			PDFEventHandler headerHandler = new PDFEventHandler();
			headerHandler.setInfo(outputFileName + author);
			pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, headerHandler);

			setPDFHeader(doc, listStudy.get(0), null);
			buildCRF4PDF(listCRF, doc, false);

			canvas.beginText();
			try {
				canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER), 8);
			} catch (IOException e) {
				e.printStackTrace();
			}

			canvas.moveText(550, 20);
			canvas.showText(Integer.toString(pdfDoc.getNumberOfPages()));
			canvas.endText();
			canvas.stroke();

			doc.close();
			pdfDoc.close();

			// setting some response headers
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + outputFileName + "\";");

			// setting the content type
			response.setContentType("application/pdf");

			// the content length
			response.setContentLength(baos.size());

			// write ByteArrayOutputStream to the ServletOutputStream
			OutputStream os = response.getOutputStream();

			baos.writeTo(os);
			baos.close();
			os.flush();
			os.close();

			// write audit
			Audit audit = new Audit();
			audit.setCategory(Audit.categories.DATA.toString());
			audit.setComment("Annotated PDF has been exported by " + httpSession.getAttribute("fullName"));
			audit.setDomain("");
			audit.setRegisteredBy(httpSession.getAttribute("uid").toString());
			audit.setSiteid(listStudy.get(0).getSiteid());
			audit.setState(Audit.states.exported.toString());
			audit.setStudyid(listStudy.get(0).getStudyid());
			audit.setVar_name("");
			audit.setScrno("");
			rpst_audit.save(audit);

		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException ie) {
			System.out.println("IE exception");
		}
	}

	private Cell createCell(Text content, float borderWidth, int colspan, TextAlignment alignment) {
		Cell cell = new Cell(1, colspan).add(new Paragraph(content));
		cell.setTextAlignment(alignment);
		return cell;
	}

	// ...
}
