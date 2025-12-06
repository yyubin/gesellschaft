package service;

import dto.SinnerResponse;
import java.util.List;

public interface SinnerService {
    SinnerResponse getSinnerById(Long id);
    SinnerResponse getSinnerByIdOrNull(Long id);
    List<SinnerResponse> getAllSinners();
}
