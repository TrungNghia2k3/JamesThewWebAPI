package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.AreaDao;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AreaServiceImplTest {

    @Mock
    private AreaDao areaDao;

    @InjectMocks
    private AreaServiceImpl areaService;

    // TEST ADD AREA
}