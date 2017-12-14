package com.omc.geode.service.api;

import com.omc.geode.service.domain.OmcGeodeServiceResult;
import com.omc.service.domain.OmcAlertDetail;
import com.omc.service.domain.OmcAlertOrigin;

public interface OmcAlertService {

	OmcGeodeServiceResult createAlertOrigin(String key, OmcAlertOrigin omcAlertOrigin);

	OmcGeodeServiceResult createAlertDetails(String key, OmcAlertDetail omcAlertDetail);

}
