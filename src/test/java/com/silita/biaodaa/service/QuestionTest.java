package com.silita.biaodaa.service;

/**
 * Created by zhushuai on 2019/5/6.
 */
public class QuestionTest extends ConfigTest {

    /*@Autowired
    TbQuestionTypeMapper tbQuestionTypeMapper;
    @Autowired
    TbQuestionInfoMapper tbQuestionInfoMapper;
    @Autowired
    TbQuestionCaseMapper tbQuestionCaseMapper;


    @org.junit.Test
    public void test() {
        TbQuestionType tbQuestionType = new TbQuestionType();
        tbQuestionType.setLevel(1);
        tbQuestionType.setName("三类人员");
        tbQuestionTypeMapper.insert(tbQuestionType);
    }

    @org.junit.Test
    public void childTest() {
        String[] strings = new String[]{"土建", "机械"};
        for (String str : strings) {
            TbQuestionType tbQuestionType = new TbQuestionType();
            tbQuestionType.setLevel(2);
            tbQuestionType.setName(str);
            tbQuestionType.setParentId(1);
            tbQuestionTypeMapper.insert(tbQuestionType);
        }
    }

    @Test
    public void importExcel() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("E:\\朱帅\\耀邦\\题库\\题库(1).xlsx")));
            XSSFSheet sheet = workbook.getSheet("土建");
            int rows = sheet.getLastRowNum();
            XSSFRow row;
            List<String> list = new ArrayList<>();
            for (int i = 1; i <= rows; i++) {
                row = sheet.getRow(i);
                TbQuestionInfo questionInfo = new TbQuestionInfo();
                questionInfo.setQuestionType(2);
                XSSFCell zeroCell = row.getCell(0);
                questionInfo.setSubType(setSubType(zeroCell.getStringCellValue()));
                XSSFCell twoCell = row.getCell(2);
                questionInfo.setAnswer(twoCell.getStringCellValue());
                XSSFCell threeCell = row.getCell(3);
                questionInfo.setSubject(threeCell.getStringCellValue());
                list = new ArrayList<>();
                XSSFCell fourCell = row.getCell(4);
                fourCell.setCellType(XSSFCell.CELL_TYPE_STRING);
                list.add(fourCell.getStringCellValue());
                XSSFCell fiveCell = row.getCell(5);
                fiveCell.setCellType(XSSFCell.CELL_TYPE_STRING);
                list.add(fiveCell.getStringCellValue());
                XSSFCell sixCell = row.getCell(6);
                if (null != sixCell) {
                    sixCell.setCellType(XSSFCell.CELL_TYPE_STRING);
                    list.add(sixCell.getStringCellValue());
                }
                XSSFCell sevenCell = row.getCell(7);
                if (null != sevenCell) {
                    sevenCell.setCellType(XSSFCell.CELL_TYPE_STRING);
                    list.add(sevenCell.getStringCellValue());
                }
                XSSFCell eightCell = row.getCell(7);
                if (null != eightCell) {
                    eightCell.setCellType(XSSFCell.CELL_TYPE_STRING);
                    list.add(eightCell.getStringCellValue());
                }
                if (null != list && list.size() > 0) {
                    questionInfo.setQuestion(JSONObject.toJSONString(list));
                }
                tbQuestionInfoMapper.insert(questionInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void caseExcel() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("E:\\朱帅\\耀邦\\题库\\题库(1).xlsx")));
            XSSFSheet sheet = workbook.getSheet("土建案例");
            int rows = sheet.getLastRowNum();
            XSSFRow row;
            for (int i = 1; i <= rows; i++) {
                row = sheet.getRow(i);
                XSSFCell cell = row.getCell(0);
                String caseName = cell.getStringCellValue();
                if (tbQuestionCaseMapper.queryCaseCount(caseName) > 0) {
                    continue;
                }
                TbQuestionCase questionCase = new TbQuestionCase();
                questionCase.setCaseName(caseName);
                tbQuestionCaseMapper.insert(questionCase);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void caseQuesExcel() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("E:\\朱帅\\耀邦\\题库\\题库(1).xlsx")));
            XSSFSheet sheet = workbook.getSheet("土建案例");
            int rows = sheet.getLastRowNum();
            XSSFRow row;
            List<String> list = new ArrayList<>();
            for (int i = 1; i <= rows; i++) {
                list = new ArrayList<>();
                TbQuestionInfo questionInfo = new TbQuestionInfo();
                questionInfo.setQuestionType(2);
                row = sheet.getRow(i);
                XSSFCell zeroCell = row.getCell(0);
                String caseName = zeroCell.getStringCellValue();
                int caseId = tbQuestionCaseMapper.queryCaseCount(caseName);
                questionInfo.setCaseId(caseId);
                XSSFCell oneCell = row.getCell(1);
                questionInfo.setSubType(setSubType(oneCell.getStringCellValue()));
                XSSFCell twoCell = row.getCell(2);
                questionInfo.setSubject(twoCell.getStringCellValue());
                XSSFCell threeCell = row.getCell(3);
                if (null != threeCell){
                    threeCell.setCellType(XSSFCell.CELL_TYPE_STRING);
                    list.add(threeCell.getStringCellValue());
                }
                XSSFCell fourCell = row.getCell(4);
                if (null != fourCell){
                    fourCell.setCellType(XSSFCell.CELL_TYPE_STRING);
                    list.add(fourCell.getStringCellValue());
                }
                XSSFCell fiveCell = row.getCell(5);
                if (null != fiveCell) {
                    fiveCell.setCellType(XSSFCell.CELL_TYPE_STRING);
                    list.add(fiveCell.getStringCellValue());
                }
                XSSFCell sixCell = row.getCell(6);
                if (null != sixCell) {
                    sixCell.setCellType(XSSFCell.CELL_TYPE_STRING);
                    list.add(sixCell.getStringCellValue());
                }
                if (null != list && list.size() > 0){
                    questionInfo.setQuestion(JSONObject.toJSONString(list));
                }
                XSSFCell sevenCell = row.getCell(7);
                questionInfo.setAnswer(sevenCell.getStringCellValue());
                tbQuestionInfoMapper.insert(questionInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private int setSubType(String value) {
        int type = 0;
        switch (value) {
            case "单选":
                type = 1;
                break;
            case "多选":
                type = 2;
                break;
            case "判断":
                type = 3;
                break;
            case "案例":
                type = 4;
                break;
            case "简答":
                type = 5;
                break;
            default:
                type = 0;
                break;
        }
        return type;
    }
}
