package sinhan.custom.shcs.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sinhan.custom.shcs.model.Lenovo;
import sinhan.custom.shcs.model.PDFExtractData;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RestAPIService {

    @Autowired
    private TypeEAConvertService typeEAConvertService;

    @Autowired
    private TypePCSConvertService typePCSConvertService;

    @Autowired
    private TypeLenovoDNConvertService typeLenovoDNConvertService;

    @Autowired
    private TypeLenovoDELConvertService typeLenovoDELConvertService;


    public List<PDFExtractData> makeExcelUsingBoschPdf(MultipartFile multipartFile, String type) {
        File file = convertFile(multipartFile);
        List<PDFExtractData> transferDatas = new ArrayList<>();
        if (type.equals("EA")) {
            transferDatas = typeEAConvertService.convertTypeEA(file);
        } else if (type.equals("PCS")) {
            transferDatas = typePCSConvertService.convertTypePCS(file);
        }
        return transferDatas;
    }

    public List<Lenovo> makeExcelUsingLenovoPdf(MultipartFile multipartFile, String type) {
        File file = convertFile(multipartFile);
        List<Lenovo> transferDatas = new ArrayList<>();
        if (type.equals("DN")) {
            transferDatas = typeLenovoDNConvertService.convertTypeLenovoDN(file);
        } else if (type.equals("DEL")) {
            transferDatas = typeLenovoDELConvertService.convertTypeLenovoDEL(file);
        }
        return transferDatas;
    }

    private File convertFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());

        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


}
