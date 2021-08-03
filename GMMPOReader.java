package com.cat.gmm.batch.reader;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.apache.log4j.Logger;
import com.cat.gmm.batch.model.TocsDTO;
import com.cat.gmm.batch.service.IGMMService;
import com.cat.gmm.batch.utils.Constants;

@Component
public class GMMPOReader implements ItemReader<List<TocsDTO>> {
	public static final Logger logger = Logger.getLogger(GMMPOReader.class);

	@Autowired
	private IGMMService gMMService;

	@PersistenceContext(unitName = "tmsUnit")
	private EntityManager dataSource;

	@Autowired
	private Environment appProperties;
	private boolean flag = true;

	@SuppressWarnings("unchecked")
	@Transactional(value = "transactionManagerTDM")
	@Override
	public List<TocsDTO> read() throws Exception {
		logger.info(Constants.READER_METHOD_START);

		List<Object[]> results = null;
		Query query = null;

		List<TocsDTO> tocsResults = null;
		if (flag) {
		try {
			Session session = (Session) dataSource.getDelegate();
			query = session.createSQLQuery(appProperties.getProperty(Constants.GET_PO_DETAILS));
			results = query.list();
			Optional nullChk = Optional.ofNullable(results);
			if (nullChk.isPresent()) {
				tocsResults = gMMService.setTocsObject(results);
			}
		    flag=false;
		} catch (Exception e) {
			e.getStackTrace();
		}
	 }
		logger.info(Constants.READER_METHOD_END);
		return tocsResults;

	}

}
