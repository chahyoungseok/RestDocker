package org.chs.restdockerapis.image.application;

import lombok.RequiredArgsConstructor;
import org.chs.domain.docker.ImageEntityRepository;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import org.chs.restdockerapis.image.application.dto.ReadImageDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageEntityRepository dockerImageRepository;

    public ReadImageDto readImage(GetRequesterDto requesterInfo, Map<String, List<String>> argCommand) {


        return null;
    }
}
