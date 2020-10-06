package sinhan.custom.shcs.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import sinhan.custom.shcs.ExcelView.BoschExcelView;
import sinhan.custom.shcs.ExcelView.LenovoExcelView;
import sinhan.custom.shcs.model.Lenovo;
import sinhan.custom.shcs.model.PDFExtractData;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ViewController {

    @GetMapping("/")
    public String main(Model model, @RequestParam(value = "param", defaultValue = "bosch") String param) {
        model.addAttribute("service", param);
        return "home";
    }

    @PostMapping("/excel/bosch")
    public ModelAndView getBoschExcel(Model model, @RequestParam("fileName") String fileName, @RequestParam("data") List<String> inputValues, HttpServletResponse response) {
        List<PDFExtractData> dataList = new ArrayList<>();
        String excelName = fileName.split(".pdf")[0] + "_converted_excel.xls";
        for (String value : inputValues) {
            String[] splitValue = value.split("\\^");
            PDFExtractData pdfExtractData = new PDFExtractData(Integer.parseInt(splitValue[0]), Integer.parseInt(splitValue[1]), splitValue[2], Double.valueOf(splitValue[3]));
            dataList.add(pdfExtractData);
        }
        model.addAttribute("rows", dataList);

        response.setContentType("application/ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=" + excelName);
        return new ModelAndView(new BoschExcelView());
    }

    @PostMapping("/excel/lenovo")
    public ModelAndView getLenovoExcel(Model model, @RequestParam("fileName") String fileName, @RequestParam("data") List<String> inputValues, HttpServletResponse response) {
        List<Lenovo> dataList = new ArrayList<>();
        String excelName = fileName.split(".pdf")[0] + "_converted_excel.xls";
        for (String value : inputValues) {
            String[] splitValue = value.split("\\^");
            Lenovo lenovo = new Lenovo(value);
            dataList.add(lenovo);
        }
        model.addAttribute("rows", dataList);

        response.setContentType("application/ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=" + excelName);
        return new ModelAndView(new LenovoExcelView());
    }

}
