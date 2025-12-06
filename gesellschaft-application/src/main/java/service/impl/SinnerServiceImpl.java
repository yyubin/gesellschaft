package service.impl;

import dto.SinnerResponse;
import exception.SinnerNotFoundException;
import lombok.RequiredArgsConstructor;
import port.SinnerRepository;
import service.SinnerService;

import java.util.List;

@RequiredArgsConstructor
public class SinnerServiceImpl implements SinnerService {

    private final SinnerRepository sinnerRepository;

    public SinnerResponse getSinnerById(Long id) {
        return sinnerRepository.findById(id)
            .map(SinnerResponse::from)
            .orElseThrow(() -> new SinnerNotFoundException("Sinner not found with id: " + id));
    }

    @Override
    public SinnerResponse getSinnerByIdOrNull(Long id) {
        return sinnerRepository.findById(id)
            .map(SinnerResponse::from)
            .orElse(null);
    }

    @Override
    public List<SinnerResponse> getAllSinners() {
        return sinnerRepository.findAll().stream()
            .map(SinnerResponse::from)
            .toList();
    }

}
