package com.whataboutmv.modules.zone;


import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    @PostConstruct
    public void initZoneData() throws IOException {
        if (zoneRepository.count() == 0) {
            ClassPathResource resource = new ClassPathResource("zones_kr.csv");

            byte[] data = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String val = new String(data, StandardCharsets.UTF_8);
            List<String> datas = Arrays.asList(val.split( "\r\n"));

            datas.stream().map(per -> per.split(",")).map(split -> Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build()).forEach(zoneRepository::save);
        }
    }
}
