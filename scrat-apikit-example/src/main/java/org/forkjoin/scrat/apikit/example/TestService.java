package org.forkjoin.scrat.apikit.example;

import org.forkjoin.TestModel;
import org.forkjoin.scrat.apikit.example.form.ValidForm;
import org.forkjoin.scrat.apikit.example.model.ObjectModel;
import org.forkjoin.scrat.apikit.example.model.child.GenericValue;
import org.forkjoin.scrat.apikit.example.type.StatusType;
import org.forkjoin.scrat.apikit.utils.ModelUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/test")
public class TestService {

    @RequestMapping("/upload")
    public StatusType upload(
            @RequestPart("file1") Mono<FilePart> file ,
            @RequestPart("file2") Mono<FilePart[]> file2 ,
            @RequestPart("file3") Mono<FilePart[]> file3 ,
            @RequestPart(value = "file2", required = false) Mono<FormFieldPart[]> formFieldPart
    ) {
        return StatusType.INIT;
    }

    /**
     * 你好
     * @param sb 参数1
     * @param param
     * @param header
     * @return
     */
    @RequestMapping("/testObject/{sb}")
    public ObjectModel testObject(
            @PathVariable String sb,
            @RequestParam String param,
            @RequestHeader String header
    ) {
        return ModelUtils.newObjectModel();
    }
//
    @RequestMapping("/testObjectDefault/{sb1}")
    public GenericValue<Date, ObjectModel> testObjectDefault(
            @Valid ValidForm form1,
            @Valid ValidForm form2,
            ValidForm form3,
            @PathVariable(name = "sb1", required = false) String sb,
            @RequestParam(name = "param1", defaultValue = "param1v", required = false) String param,
            @RequestParam(name = "param2", defaultValue = "1", required = false) Integer param2,
            @RequestParam(name = "param3", defaultValue = "1", required = false) Double param3,
            @RequestParam(name = "param4", defaultValue = "true", required = false) Boolean param4,
            @RequestParam(name = "param5", defaultValue = "1", required = false) Float param5,
            @RequestParam(name = "date", defaultValue = "2010-01-01", required = false) LocalDate date,
            @RequestHeader(name = "header1", defaultValue = "header1v", required = false) String header
    ) {
        return null;
    }
//
//    /**
//     * 呵呵
//     *
//     * @return 返回当前时间
//     */
//    @RequestMapping("/instant")
//    public Instant instant() {
//        return Instant.now();
//    }
//
//    @RequestMapping("/instantArray")
//    public Instant[] instantsArray() {
//        return new Instant[]{Instant.now(),Instant.now()};
//    }
//
//    @RequestMapping("/instants")
//    public List<Instant> instants() {
//        return Arrays.asList(Instant.now());
//    }
//
//    @RequestMapping("/longs")
//    public List<Long> longs() {
//        return Arrays.asList(Long.MAX_VALUE);
//    }
//
//    @RequestMapping("/long")
//    public Long longValue() {
//        return Long.MAX_VALUE;
//    }
//
//    @RequestMapping("/valid")
//    public ValidModel valid(@Valid ValidForm form) {
//        ValidModel m = new ValidModel();
//        BeanUtils.copyProperties(form, m);
//        return m;
//    }
//
//    @RequestMapping("/object")
//    public ObjectModel object(@Valid ObjectForm form) {
//        ObjectModel m = new ObjectModel();
//        TestArrayModel testArray = new TestArrayModel();
//        TestModel test = new TestModel();
//        TestWrapperModel testWrapper = new TestWrapperModel();
//
//        m.setTestArray(testArray);
//        m.setTest(test);
//        m.setTestWrapper(testWrapper);
//
//        if (form.getTest() != null) {
//            BeanUtils.copyProperties(form.getTest(), test);
//        }
//        if (form.getTestArray() != null) {
//            BeanUtils.copyProperties(form.getTestArray(), testArray);
//        }
//        if (form.getTestWrapper() != null) {
//            BeanUtils.copyProperties(form.getTestWrapper(), testWrapper);
//        }
//
//        return m;
//    }
//

//
    @RequestMapping("/testModel")
    public TestModel<Date> testModel() {
        return new TestModel();
    }
//
//    /**
//     * form实际上不支持泛型
//     */
//    @RequestMapping("/generic")
//    public GenericModel<ValidModel> generic(@Valid GenericForm<ValidForm> form) {
//        GenericModel<ValidModel> model = new GenericModel<>();
//        ValidModel data = new ValidModel();
//        data.setName(RandomStringUtils.random(128));
//        model.setData(data);
//        return model;
//    }
}
