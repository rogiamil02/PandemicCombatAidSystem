package controller;

import package.model.Intercambio;
import package.model.dto.IntercambioHospitalDTO;
import package.service.IntercambioService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/intercambios")
public class IntercambioController {

    private IntercambioService service;

    public IntercambioController(IntercambioService service) {
        this.service = service;
    }

    @PostMapping
    public List<Intercambio> salvar (@RequestBody IntercambioHospitalDTO intercambioHospitalDTO) {
        return service.intercambioRecursos(intercambioHospitalDTO);
    }


}
