package sinhan.custom.shcs.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import sinhan.custom.shcs.ExcelView.ExcelView;
import sinhan.custom.shcs.model.PDFExtractData;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ViewController {

    @GetMapping("/")
    public ModelAndView main() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("home");
        return modelAndView;
    }

    @PostMapping("/excel")
    public ModelAndView getExcel(Model model, @RequestParam("fileName") String fileName, @RequestParam("data") List<String> inputValues, HttpServletResponse response) {
        List<PDFExtractData> dataList = new ArrayList<>();
        String excelName = fileName.split(".pdf")[0] + "_converted_excel.xls";
        for (String value : inputValues) {
            String[] splitValue = value.split(",");
            PDFExtractData pdfExtractData = new PDFExtractData(Integer.parseInt(splitValue[0]), Integer.parseInt(splitValue[1]), splitValue[2], Double.valueOf(splitValue[3]));
            dataList.add(pdfExtractData);
        }
        model.addAttribute("rows", dataList);

        response.setContentType("application/ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=" + excelName);
        return new ModelAndView(new ExcelView());
    }

}
