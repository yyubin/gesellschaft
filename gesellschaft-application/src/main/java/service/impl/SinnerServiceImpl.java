package service.impl;

import dto.SinnerResponse;
import exception.SinnerNotFoundException;
import lombok.RequiredArgsConstructor;
import port.SinnerRepository;
import service.SinnerService;

@RequiredArgsConstructor
public class SinnerServiceImpl implements SinnerService {

    private final SinnerRepository sinnerRepository;

    public SinnerResponse getSinnerById(Long id) {
        return sinnerRepository.findById(id)
            .map(SinnerResponse::from)
            .orElseThrow(() -> new SinnerNotFoundException("Sinner not found with id: " + id));
    }

}
