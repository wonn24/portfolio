

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpSession;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ImportController {
	@Autowired
	private StudyRepository rpst_study;

	@Autowired
	private DMRepository rpst_dm;

	@Autowired
	AuditRepository rpst_audit;

	@Autowired
	private ParticipantRepository rpst_participant;

	@Autowired
	FlagRepository rpst_flag;

	List<String> stopwords = Stream.of("id").collect(Collectors.toList());

	@PostMapping(value = "/../{...}")
	public ResponseEntity<?> validateExcelCRF(@PathVariable String ..., @RequestParam("...") MultipartFile file, HttpSession httpSession) {
		// call study repository
		List<Study> listStudy = rpst_study.findAllByShortname(studyShortName);

		if (listStudy.size() == 0) {
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException ex) {
				ex.printStackTrace();
			}
			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}

		try {
			Map<Object, Map<Object, Object>> mapResult = verifyExcel(file);

			if (mapResult == null) {
				String jsonResult = null;
				try {
					Map<Object, Object> map = new HashMap<>();
					map.put("hasError", true);

					mapResult = new HashMap<>();
					mapResult.put("meta", map);

					HashMap<Object, Object> mapData = new HashMap<>();
					mapData.put("result", "error");
					mapData.put("data", mapResult);
					ObjectMapper mapper = new ObjectMapper();
					jsonResult = mapper.writeValueAsString(mapData);

				} catch (JsonProcessingException ex) {
					ex.printStackTrace();
				}

				return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
			}

			String jsonResult = null;
			ObjectMapper mapper = null;
			HashMap<Object, Object> mapData = new HashMap<>();
			mapData.put("result", "success");
			mapData.put("data", mapResult);
			mapper = new ObjectMapper();
			jsonResult = mapper.writeValueAsString(mapData);

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		} catch (IOException e) {

			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException ex) {
				ex.printStackTrace();
			}

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/.../{...}")
	public ResponseEntity<?> validateExcelAudit(@PathVariable String ..., @RequestParam("...") MultipartFile file, HttpSession httpSession) {
		// call study repository
		List<Study> listStudy = rpst_study.findAllByShortname(studyShortName);

		if (listStudy.size() == 0) {
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException ex) {
				ex.printStackTrace();
			}
			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}

		try {
			Map<Object, Map<Object, Object>> mapResult = verifyExcel(file);

			if (mapResult == null) {
				String jsonResult = null;
				try {
					Map<Object, Object> map = new HashMap<>();
					map.put("hasError", true);

					mapResult = new HashMap<>();
					mapResult.put("meta", map);

					HashMap<Object, Object> mapData = new HashMap<>();
					mapData.put("result", "error");
					mapData.put("data", mapResult);
					ObjectMapper mapper = new ObjectMapper();
					jsonResult = mapper.writeValueAsString(mapData);

				} catch (JsonProcessingException ex) {
					ex.printStackTrace();
				}

				return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
			}

			String jsonResult = null;
			ObjectMapper mapper = null;
			HashMap<Object, Object> mapData = new HashMap<>();
			mapData.put("result", "success");
			mapData.put("data", mapResult);
			mapper = new ObjectMapper();
			jsonResult = mapper.writeValueAsString(mapData);

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		} catch (IOException e) {

			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException ex) {
				ex.printStackTrace();
			}

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/.../{...}")
	public ResponseEntity<?> validateExcelFlag(@PathVariable String ..., @RequestParam("...") MultipartFile file, HttpSession httpSession) {
		// call study repository
		List<Study> listStudy = rpst_study.findAllByShortname(studyShortName);

		if (listStudy.size() == 0) {
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException ex) {
				ex.printStackTrace();
			}
			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}

		try {
			Map<Object, Map<Object, Object>> mapResult = verifyExcel(file);

			if (mapResult == null) {
				String jsonResult = null;
				try {
					Map<Object, Object> map = new HashMap<>();
					map.put("hasError", true);

					mapResult = new HashMap<>();
					mapResult.put("meta", map);

					HashMap<Object, Object> mapData = new HashMap<>();
					mapData.put("result", "error");
					mapData.put("data", mapResult);
					ObjectMapper mapper = new ObjectMapper();
					jsonResult = mapper.writeValueAsString(mapData);

				} catch (JsonProcessingException ex) {
					ex.printStackTrace();
				}

				return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
			}

			String jsonResult = null;
			ObjectMapper mapper = null;
			HashMap<Object, Object> mapData = new HashMap<>();
			mapData.put("result", "success");
			mapData.put("data", mapResult);
			mapper = new ObjectMapper();
			jsonResult = mapper.writeValueAsString(mapData);

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		} catch (IOException e) {

			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException ex) {
				ex.printStackTrace();
			}

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}
	}
	
	private Map<Object, Object> parseSheet(Sheet sheet, Field[] fields) {
		StringBuilder header = new StringBuilder();
		int count = 0;
		for (Field f : fields) {
			String name = f.getName();

			if (name.contains("_comment"))
				continue;

			if (hasStopWord(name))
				continue;

			if (header.length() == 0)
				header.append(f.getName());
			else
				header.append(",").append(f.getName());

			count++;
		}

		ArrayList<Object> listCells = new ArrayList<>(); // header
		ArrayList<Object> listErrors = new ArrayList<>(); // error
		Map<Object, Object> map = new HashMap<>(); // to be returned

		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (row.getRowNum() == 0) {
				for (int i = 0; i < count; i++) {
					try {
						Cell cell = row.getCell(i);
						CellType type = (cell == null) ? CellType.BLANK : cell.getCellType();

						switch (type) {
						case BLANK:
							listCells.add("");
							break;
						case NUMERIC:
							listCells.add(Integer.valueOf((int) row.getCell(i).getNumericCellValue()));
							break;
						case STRING:
							listCells.add(row.getCell(i).getStringCellValue());
							break;
						default:
							break;
						}
					} catch (Exception e) {
						listErrors.add(row.getRowNum()); // add row number
					}
				}
				map.put("header_count", count);
			} else {
				for (int i = 0; i < count; i++) {
					try {
						Cell cell = row.getCell(i);
						CellType type = (cell == null) ? CellType.BLANK : cell.getCellType();

						switch (type) {
						case BLANK:
							listCells.add("");
							break;
						case NUMERIC:
							listCells.add(Integer.valueOf((int) row.getCell(i).getNumericCellValue()));
							break;
						case STRING:
							listCells.add(row.getCell(i).getStringCellValue());
							break;
						default:
							break;
						}

					} catch (Exception e) {
						listErrors.add(row.getRowNum()); // add row number
					}
				}
			}

			map.put("error_count", listErrors.size());
			if (listErrors.size() > 0)
				map.put("errors", listErrors);
			else
				map.put("errors", 0);
		}
		return map;
	}

	@PostMapping(value = "/.../{...}")
	public ResponseEntity<?> importExcelsCRF(@PathVariable String ..., @RequestParam("...") MultipartFile file, HttpSession httpSession) {
		// call study repository
		System.out.println("importing excel crf");
		List<Study> listStudy = rpst_study.findAllByShortname(studyShortName);

		if (listStudy.size() == 0) {
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);
			} catch (JsonProcessingException ex) {
				ex.printStackTrace();
			}
			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}

		String uid = httpSession.getAttribute("uid").toString();

		try {
			Map<Object, Map<Object, Object>> mapResult = importExcelCRF(file, httpSession, false);

			// write audit
			try {
				Audit audit = new Audit();
				audit.setCategory(Audit.categories.DATA.toString());
				audit.setComment("...");
				audit.setDomain("");
				audit.setRegisteredBy(uid);
				audit.setSiteid(listStudy.get(0).getSiteid());
				audit.setState(Audit.states.imported.toString());
				audit.setStudyid(listStudy.get(0).getStudyid());
				audit.setVar_name("");
				audit.setScrno("");
				rpst_audit.save(audit);
			} catch (Exception e) {
				e.getStackTrace();
			}

			String jsonResult = null;
			ObjectMapper mapper = null;
			HashMap<Object, Object> mapData = new HashMap<>();
			mapData.put("result", "success");
			mapData.put("data", mapResult);
			mapper = new ObjectMapper();
			jsonResult = mapper.writeValueAsString(mapData);

			System.out.println(">> Import done.");

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		} catch (IOException e) {
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException ex) {
				ex.printStackTrace();
			}
			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/.../{...}")
	public ResponseEntity<?> importExcelsCRFwithFlag(@PathVariable String ..., @RequestParam("...") MultipartFile file, HttpSession httpSession) {
		// call study repository
		List<Study> listStudy = rpst_study.findAllByShortname(studyShortName);

		if (listStudy.size() == 0) {
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);
			} catch (JsonProcessingException ex) {
				ex.printStackTrace();
			}
			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}

		String uid = httpSession.getAttribute("uid").toString();
		try {
			Map<Object, Map<Object, Object>> mapResult = importExcelCRF(file, httpSession, true);

			// write audit
			try {
				Audit audit = new Audit();
				audit.setCategory(...);
				audit.setComment("...");
				audit.setDomain("...");
				audit.setRegisteredBy(...);
				audit.setSiteid(...);
				audit.setState(...);
				audit.setStudyid(...);
				audit.setVar_name("...");
				audit.setScrno("...");
				rpst_audit.save(audit);
			} catch (Exception e) {
				e.getStackTrace();
			}

			String jsonResult = null;
			ObjectMapper mapper = null;
			HashMap<Object, Object> mapData = new HashMap<>();
			mapData.put("result", "success");
			mapData.put("data", mapResult);
			mapper = new ObjectMapper();
			jsonResult = mapper.writeValueAsString(mapData);

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		} catch (IOException e) {
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException ex) {
				ex.printStackTrace();
			}
			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/.../{...}")
	public ResponseEntity<?> importExcelsAudit(@PathVariable String ..., @RequestParam("...") MultipartFile file, HttpSession httpSession) {
		// call study repository
		List<Study> listStudy = rpst_study.findAllByShortname(studyShortName);

		if (listStudy.size() == 0) {
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);
			} catch (JsonProcessingException ex) {
				ex.printStackTrace();
			}
			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}

		String uid = httpSession.getAttribute("uid").toString();
		try {
			Map<Object, Map<Object, Object>> mapResult = importExcelCRF(file, httpSession, false);

			// write audit
			try {
				Audit audit = new Audit();
				audit.setCategory(...);
				audit.setComment("...");
				audit.setDomain("...");
				audit.setRegisteredBy(...);
				audit.setSiteid(...);
				audit.setState(...);
				audit.setStudyid(...);
				audit.setVar_name("...");
				audit.setScrno("...");
				rpst_audit.save(audit);
			} catch (Exception e) {
				e.getStackTrace();
			}

			String jsonResult = null;
			ObjectMapper mapper = null;
			HashMap<Object, Object> mapData = new HashMap<>();
			mapData.put("result", "success");
			mapData.put("data", mapResult);
			mapper = new ObjectMapper();
			jsonResult = mapper.writeValueAsString(mapData);

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		} catch (IOException e) {
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException ex) {
				ex.printStackTrace();
			}
			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/.../{...}")
	public ResponseEntity<?> importExcelsFlag(@PathVariable String ..., @RequestParam("...") MultipartFile file, HttpSession httpSession) {
		// call study repository
		List<Study> listStudy = rpst_study.findAllByShortname(studyShortName);

		if (listStudy.size() == 0) {
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);
			} catch (JsonProcessingException ex) {
				ex.printStackTrace();
			}
			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}

		String uid = httpSession.getAttribute("uid").toString();
		try {
			Map<Object, Map<Object, Object>> mapResult = importExcelCRF(file, httpSession, false);

			// write audit
			try {
				Audit audit = new Audit();
				audit.setCategory(...);
				audit.setComment("...");
				audit.setDomain("...");
				audit.setRegisteredBy(...);
				audit.setSiteid(...);
				audit.setState(...);
				audit.setStudyid(...);
				audit.setVar_name("...");
				audit.setScrno("...");
				rpst_audit.save(audit);
			} catch (Exception e) {
				e.getStackTrace();
			}

			String jsonResult = null;
			ObjectMapper mapper = null;
			HashMap<Object, Object> mapData = new HashMap<>();
			mapData.put("result", "success");
			mapData.put("data", mapResult);
			mapper = new ObjectMapper();
			jsonResult = mapper.writeValueAsString(mapData);

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		} catch (IOException e) {
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException ex) {
				ex.printStackTrace();
			}
			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}
	}

	// ...

}
