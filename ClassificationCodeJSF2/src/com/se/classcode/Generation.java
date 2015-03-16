package com.se.classcode;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import Util.DataBaseConnection;
import DTOs.ClassRol;
import DTOs.ShFeature;
import DTOs.TblQchApprovedValues;
import DTOs.XlpSeFeature;
import DTOs.XlpSeManufacturer;
import DTOs.XlpSePl;

public class Generation {
	public static void main(String args[]) {
		Session session = DataBaseConnection.getSession();
		// generateRolesByPL("ATM UNI",130);
		// generateByPl("Connector Circular", 170, null, "10.0.1.48", session);
		session.close();
	}

	public static void generateByPl(String plName, int classID, String manName,
			String ip, Session session) {
		session.createSQLQuery(
				"delete from importer.part_code_temp where ip ='" + ip + "'")
				.executeUpdate();
		if (!session.getTransaction().isInitiator())
			session.beginTransaction();
		session.getTransaction().commit();
		XlpSeManufacturer man = null;
		if (manName != null) {
			Criteria crit = session.createCriteria(XlpSeManufacturer.class);
			crit.add(Restrictions.eq("manName", manName));
			man = (XlpSeManufacturer) crit.uniqueResult();
		}
		Criteria criteria = session.createCriteria(XlpSePl.class);
		criteria.add(Restrictions.eq("plName", plName));
		XlpSePl pl = (XlpSePl) criteria.uniqueResult();
		List count = session.createSQLQuery(
				"select  code, REF_URL from importer.clas_rolls where pl_id="
						+ pl.getPlId() + " and clas_id=" + classID).list();
		if (count.isEmpty()) {
			if (manName == null) {
				session.createSQLQuery(
						"insert into importer.part_code_temp (ip,COM_ID,CLAS_ID,DYN_CLASS,REF_URL,manual_flag,round,MODIFY_DATE,PL_id) select '"
								+ ip
								+ "',com_id ,"
								+ classID
								+ ",'','',0,'5',sysdate,cm.get_pl_id('"
								+ plName
								+ "') from(select com_id from cm.xlp_se_component where pl_id=cm.get_pl_id('"
								+ plName + "')" + ")").executeUpdate();

				if (!session.getTransaction().isInitiator())
					session.beginTransaction();
				session.getTransaction().commit();
			} else {

				session.createSQLQuery(
						"insert into importer.part_code_temp (ip,COM_ID,CLAS_ID,DYN_CLASS,REF_URL,manual_flag,round,MODIFY_DATE,PL_id) select '"
								+ ip
								+ "' ,com_id ,"
								+ classID
								+ ",'','',0,'5',sysdate,cm.get_pl_id('"
								+ plName
								+ "') from(select com_id from cm.xlp_se_component where man_id= cm.get_man_id('"
								+ manName
								+ "') and pl_id=cm.get_pl_id('"
								+ plName + "'))").executeUpdate();
				if (!session.getTransaction().isInitiator())
					session.beginTransaction();
				session.getTransaction().commit();
			}
		} else {

			List codeNames = session.createSQLQuery(
					"select CLAS_NAME from importer.CLASSIFICATION_CODE where CLAS_ID ="
							+ classID).list();
			if (!codeNames.isEmpty()) {
				String codeName = codeNames.get(0).toString();
				if (codeName.equals("ECCN")) {
					// generateRad(pl, man, classID);
				}
			}

			if (count.size() == 1) {
				Object[] row = (Object[]) count.get(0);

				String code = "";
				String ref = "";
				if (row[0] != null) {
					code = row[0].toString();
				}
				if (row[1] != null) {
					ref = row[1].toString();
				}
				if (manName == null) {

					session.createSQLQuery(
							"insert into importer.part_code_temp (ip,COM_ID,CLAS_ID,DYN_CLASS,REF_URL,manual_flag,MODIFY_DATE,PL_id,ROUND) select '"
									+ ip
									+ "',com_id ,"
									+ classID
									+ ",'"
									+ code
									+ "','"
									+ ref
									+ "',0,sysdate,cm.get_pl_id('"
									+ plName
									+ "'),2 from(select com_id from cm.xlp_se_component where pl_id=cm.get_pl_id('"
									+ plName + "'))").executeUpdate();
					if (!session.getTransaction().isInitiator())
						session.beginTransaction();
					session.getTransaction().commit();

				} else {

					session.createSQLQuery(
							"insert into importer.part_code_temp (ip,COM_ID,CLAS_ID,DYN_CLASS,REF_URL,manual_flag,round,MODIFY_DATE,PL_id) select '"
									+ ip
									+ "',com_id ,"
									+ classID
									+ ",'"
									+ code
									+ "','"
									+ ref
									+ "',0,2,sysdate,cm.get_pl_id('"
									+ plName
									+ "') from(select com_id from cm.xlp_se_component where man_id= cm.get_man_id('"
									+ manName
									+ "') and pl_id=cm.get_pl_id('"
									+ plName + "'))").executeUpdate();
					if (!session.getTransaction().isInitiator())
						session.beginTransaction();
					session.getTransaction().commit();
				}
			} else {
				int num = 0;
				session.createSQLQuery(
						"delete from importer.sh_feature where ip ='" + ip
								+ "'").executeUpdate();
				if (!session.getTransaction().isInitiator())
					session.beginTransaction();
				session.getTransaction().commit();
				List fet = session
						.createSQLQuery(
								"select SE_PARAMETERS_NAME_1, SE_PARAMETERS_NAME_2, SE_PARAMETERS_NAME_3, SE_PARAMETERS_NAME_4, SE_PARAMETERS_NAME_5 from importer.clas_rolls where pl_id="
										+ pl.getPlId()
										+ " and CLAS_ID= "
										+ classID).list();

				Set set = new HashSet();
				for (int i = 0; i < fet.size(); i++) {
					Object[] row = (Object[]) fet.get(i);
					for (int j = 0; j < row.length; j++) {
						String fetName = (String) row[j];
						if (fetName != null) {
							set.add(fetName);
						}
					}
				}
				insertShValues(set, pl, ip, session);

				List rules = session
						.createSQLQuery(
								"select CODE,REF_URL,SE_PARAMETERS_NAME_1,START_COND_1,START_VALUE_1 ,START_MULTIPLIER_1,START_UNIT_1,END_COND_1,END_VALUE_1 ,END_MULTIPLIER_1,END_UNIT_1, SE_PARAMETERS_NAME_2,START_COND_2,START_VALUE_2 ,START_MULTIPLIER_2,START_UNIT_2 ,END_COND_2, END_VALUE_2, END_MULTIPLIER_2 ,END_UNIT_2 ,SE_PARAMETERS_NAME_3,START_COND_3, START_VALUE_3,START_MULTIPLIER_3 ,START_UNIT_3 ,END_COND_3 ,END_VALUE_3 ,END_MULTIPLIER_3 ,END_UNIT_3 ,SE_PARAMETERS_NAME_4 ,START_COND_4 ,START_VALUE_4 ,START_MULTIPLIER_4 ,START_UNIT_4 ,  END_COND_4 ,END_VALUE_4 ,END_MULTIPLIER_4 ,END_UNIT_4 ,SE_PARAMETERS_NAME_5 ,START_COND_5,START_VALUE_5 ,START_MULTIPLIER_5,START_UNIT_5 ,END_COND_5 ,END_VALUE_5 ,END_MULTIPLIER_5 ,END_UNIT_5 ,FEATURE_ID_1 ,FEATURE_ID_2,FEATURE_ID_3,FEATURE_ID_4 ,FEATURE_ID_5  from importer.clas_rolls where pl_id=cm.get_pl_id('"
										+ plName
										+ "') and clas_id="
										+ classID
										+ " order by EXECUTION_ORDER ").list();
				Criteria crit = session.createCriteria(ClassRol.class);

				Object[] da = (Object[]) rules.get(0);
				String fast_date_code = da[0].toString();

				String fast_ref = "";
				if (da[1] != null) {
					fast_ref = da[1].toString();
				}
				for (int r = 0; r < rules.size(); r++) {
					Object[] rol = (Object[]) rules.get(r);

					String code = rol[0] == null ? "" : rol[0].toString();
					String refURL = rol[1] == null ? "" : rol[1].toString();

					String fet1 = rol[2] == null ? "" : rol[2].toString();
					String startCond1 = rol[3] == null ? "" : rol[3].toString();
					String startValue1 = rol[4] == null ? "" : rol[4]
							.toString();
					String startMulti1 = rol[5] == null ? "" : rol[5]
							.toString();
					String startUnit1 = rol[6] == null ? "" : rol[6].toString();
					String endCond1 = rol[7] == null ? "" : rol[7].toString();
					String endVal1 = rol[8] == null ? "" : rol[8].toString();
					String endMulti1 = rol[9] == null ? "" : rol[9].toString();
					String endUnit1 = rol[10] == null ? "" : rol[10].toString();
					boolean flag = false;
					if ((r == rules.size() - 1) && (fet1.equals(""))) {
						if (manName == null) {

							session.createSQLQuery(
									"insert into importer.part_code_temp (ip,COM_ID,PL_ID,CLAS_ID,DYN_CLASS,MODIFY_DATE,round,manual_flag,ref_url) select '"
											+ ip
											+ "', com_id,cm.get_pl_id('"
											+ plName
											+ "'),"
											+ classID
											+ ",'"
											+ code
											+ "',sysdate,2,0,'"
											+ refURL
											+ "' from (select com_id from cm.dynamic_flat where pl_id=cm.get_pl_id('"
											+ plName
											+ "') minus select com_id from importer.part_code_temp where pl_id= cm.get_pl_id('"
											+ plName
											+ "') and ip ='"
											+ ip
											+ "' )").executeUpdate();
							if (!session.getTransaction().isInitiator())
								session.beginTransaction();
							session.getTransaction().commit();

						} else {

							session.createSQLQuery(
									"insert into importer.part_code_temp (ip,COM_ID,PL_ID,CLAS_ID,DYN_CLASS,MODIFY_DATE,manual_flag,round,ref_url) select '"
											+ ip
											+ "' , com_id,cm.get_pl_id('"
											+ plName
											+ "'),"
											+ classID
											+ ",'"
											+ code
											+ "',sysdate,2,0,'"
											+ refURL
											+ "' from (select com_id from cm.dynamic_flat where man_id=cm.get_man_id('"
											+ manName
											+ "') and pl_id=cm.get_pl_id('"
											+ plName
											+ "') minus (select com_id from importer.part_code_temp where ip='"
											+ ip
											+ "' and pl_id =cm.get_pl_id('"
											+ plName
											+ "') and CLAS_ID="
											+ classID + "))").executeUpdate();

							if (!session.getTransaction().isInitiator())
								session.beginTransaction();
							session.getTransaction().commit();
						}
					} else {
						String fet2 = rol[11] == null ? "" : rol[11].toString();
						String startCond2 = rol[12] == null ? "" : rol[12]
								.toString();
						String startValue2 = rol[13] == null ? "" : rol[13]
								.toString();
						String startMulti2 = rol[14] == null ? "" : rol[14]
								.toString();
						String startUnit2 = rol[15] == null ? "" : rol[15]
								.toString();
						String endCond2 = rol[16] == null ? "" : rol[16]
								.toString();
						String endVal2 = rol[17] == null ? "" : rol[17]
								.toString();
						String endMulti2 = rol[18] == null ? "" : rol[18]
								.toString();
						String endUnit2 = rol[19] == null ? "" : rol[19]
								.toString();

						String fet3 = rol[20] == null ? "" : rol[20].toString();
						String startCond3 = rol[21] == null ? "" : rol[21]
								.toString();
						String startValue3 = rol[22] == null ? "" : rol[22]
								.toString();
						String startMulti3 = rol[23] == null ? "" : rol[23]
								.toString();
						String startUnit3 = rol[24] == null ? "" : rol[24]
								.toString();
						String endCond3 = rol[25] == null ? "" : rol[25]
								.toString();
						String endVal3 = rol[26] == null ? "" : rol[26]
								.toString();
						String endMulti3 = rol[27] == null ? "" : rol[27]
								.toString();
						String endUnit3 = rol[28] == null ? "" : rol[28]
								.toString();

						String fet4 = rol[29] == null ? "" : rol[29].toString();
						String startCond4 = rol[30] == null ? "" : rol[30]
								.toString();
						String startValue4 = rol[31] == null ? "" : rol[31]
								.toString();
						String startMulti4 = rol[32] == null ? "" : rol[32]
								.toString();
						String startUnit4 = rol[33] == null ? "" : rol[33]
								.toString();
						String endCond4 = rol[34] == null ? "" : rol[34]
								.toString();
						String endVal4 = rol[35] == null ? "" : rol[35]
								.toString();
						String endMulti4 = rol[36] == null ? "" : rol[36]
								.toString();
						String endUnit4 = rol[37] == null ? "" : rol[37]
								.toString();

						String fet5 = rol[38] == null ? "" : rol[38].toString();
						String startCond5 = rol[39] == null ? "" : rol[39]
								.toString();
						String startValue5 = rol[40] == null ? "" : rol[40]
								.toString();
						String startMulti5 = rol[41] == null ? "" : rol[41]
								.toString();
						String startUnit5 = rol[42] == null ? "" : rol[42]
								.toString();
						String endCond5 = rol[43] == null ? "" : rol[43]
								.toString();
						String endVal5 = rol[44] == null ? "" : rol[44]
								.toString();
						String endMulti5 = rol[45] == null ? "" : rol[45]
								.toString();
						String endUnit5 = rol[46] == null ? "" : rol[46]
								.toString();

						String fetID1 = rol[47] == null ? "" : rol[47]
								.toString();
						String fetID2 = rol[48] == null ? "" : rol[48]
								.toString();
						String fetID3 = rol[49] == null ? "" : rol[49]
								.toString();
						String fetID4 = rol[50] == null ? "" : rol[50]
								.toString();
						String fetID5 = rol[51] == null ? "" : rol[51]
								.toString();

						session.createSQLQuery(
								"delete from importer.value_id where ip ='"
										+ ip + "'").executeUpdate();
						if (!session.getTransaction().isInitiator())
							session.beginTransaction();
						session.getTransaction().commit();
						String condition = "";
						condition = condition
								+ getWhereCondition(fet1, startCond1,
										startUnit1, endUnit1, endCond1,
										startValue1, endVal1, startMulti1,
										endMulti1, pl, ip, session);
						if (!fet2.equals("")) {
							String string = getWhereCondition(fet2, startCond2,
									startUnit2, endUnit2, endCond2,
									startValue2, endVal2, startMulti2,
									endMulti2, pl, ip, session);
							if (!string.equals("")) {
								condition = condition + " and " + string;
							}

							if (!fet3.equals("")) {
								String string1 = getWhereCondition(fet3,
										startCond3, startUnit3, endUnit3,
										endCond3, startValue3, endVal3,
										startMulti3, endMulti3, pl, ip, session);
								if (!string1.equals("")) {
									condition = condition + " and " + string1;
								}
								if (!fet4.equals("")) {
									String string2 = getWhereCondition(fet4,
											startCond4, startUnit4, endUnit4,
											endCond4, startValue4, endVal4,
											startMulti4, endMulti4, pl, ip,
											session);
									if (!string2.equals("")) {
										condition = condition + " and "
												+ string2;
									}
									if (!fet5.equals("")) {
										String string3 = getWhereCondition(
												fet5, startCond5, startUnit5,
												endUnit5, endCond5,
												startValue5, endVal5,
												startMulti5, endMulti5, pl, ip,
												session);
										if (!string3.equals("")) {
											condition = condition + " and "
													+ string3;
										}
									}
								}
							}
						}

						if ((!condition.trim().contains("()"))
								&& (!condition.contains("false"))) {

							if (manName == null) {

								String sql = "INSERT INTO importer.part_code_temp (ip,com_id, pl_id, CLAS_ID, DYN_CLASS,round, manual_flag,ref_url,MODIFY_DATE) (SELECT '"
										+ ip
										+ "',com_id, cm.get_pl_id ('"
										+ plName
										+ "'),"
										+ classID
										+ ",'"
										+ code
										+ "', 2,0,'"
										+ refURL
										+ "',sysdate FROM  (SELECT   com_id FROM  cm.dynamic_flat WHERE pl_id = cm.get_pl_id ('"
										+ plName
										+ "') and "
										+ condition
										+ "  MINUS (SELECT com_id FROM   importer.part_code_temp WHERE  ip = '"
										+ ip
										+ "'   and clas_id ="
										+ classID
										+ " and pl_id = cm.get_pl_id ('"
										+ plName + "'))))";
								System.out.println(sql);
								session.createSQLQuery(sql).executeUpdate();
								if (!session.getTransaction().isInitiator())
									session.beginTransaction();
								session.getTransaction().commit();

							} else {

								String sql = "INSERT INTO importer.part_code_temp (ip,com_id, pl_id, CLAS_ID, DYN_CLASS, manual_flag,round,ref_url,MODIFY_DATE) (SELECT '"
										+ ip
										+ "',com_id, cm.get_pl_id ('"
										+ plName
										+ "'),"
										+ classID
										+ ",'"
										+ code
										+ "',0,2,'"
										+ refURL
										+ "',sysdate FROM  (SELECT   com_id FROM  cm.dynamic_flat WHERE man_id=cm.get_man_id('"
										+ manName
										+ "') and pl_id = cm.get_pl_id ('"
										+ plName
										+ "') and "
										+ condition
										+ "  MINUS (SELECT com_id FROM   importer.part_code_temp WHERE ip='"
										+ ip
										+ "' and pl_id = cm.get_pl_id ('"
										+ plName + "'))))";
								System.out.println(sql);
								session.createSQLQuery(sql).executeUpdate();

							}
						}
						// else if (condition.equals("true")) {
						//
						// if (manName == null) {
						// if (!endCond1.equals("")) {
						//
						// session.createSQLQuery(
						// "insert into importer.part_code_temp (ip,COM_ID,CLAS_ID,DYN_CLASS,REF_URL,round,manual_flag,MODIFY_DATE,PL_id) select com_id,"
						// + classID
						// + ",'"
						// + code
						// + "','"
						// + refURL
						// + "',2,0,sysdate,"
						// + pl.getPlId()
						// +
						// " from importer.gen_rad where com_id in (select com_id from(select com_id ,cm.is_num(r_val) chkk from importer.gen_rad )where chkk=0) and cm.tonumeric (r_val) "
						// + startCond1
						// + startValue1
						// + " AND cm.tonumeric (r_val) "
						// + endCond1
						// + endVal1
						// + " and cm.tonumeric(r_val)"
						// + endCond1
						// + endVal1
						// +
						// " minus  (select com_id from cm.part_codewhere clas_id ="
						// + classID
						// + " and  pl_id ="
						// + pl.getPlId() + ")")
						// .executeUpdate();
						// if (!session.getTransaction().isInitiator())
						// session.beginTransaction();
						// session.getTransaction().commit();
						//
						// } else {
						// String que = "update cm.part_codeset pl_id= "
						// + pl.getPlId()
						// + " ,DYN_CLASS='"
						// + code
						// + "',REF_URL='"
						// + refURL
						// +
						// "',round=2,SE_CLASS=STATIC_CLASS,ref='EXCEPTION', MODIFY_DATE=sysdate where clas_id="
						// + classID
						// +
						// " and com_id in(SELECT com_id FROM  importer.gen_rad WHERE com_id IN(SELECT com_id FROM (SELECT com_id, cm.is_num (r_val) chkk FROM   importer.gen_rad)WHERE   chkk = 0)AND cm.tonumeric (r_val) "
						// + startCond1
						// + startValue1
						// +
						// "  and com_id in (select com_id from cm.part_codewhere pl_id="
						// + pl.getPlId()
						// + " and clas_id ="
						// + classID
						// + " and manual_flag = 1))";
						//
						// session.createSQLQuery(que).executeUpdate();
						// if (!session.getTransaction().isInitiator())
						// session.beginTransaction();
						// session.getTransaction().commit();
						// session.createSQLQuery(
						// "update cm.part_codeset pl_id= "
						// + pl.getPlId()
						// + " ,DYN_CLASS='"
						// + code
						// + "',REF_URL='"
						// + refURL
						// + "',round=2, MODIFY_DATE=sysdate where clas_id="
						// + classID
						// +
						// " and com_id in(SELECT com_id FROM  importer.gen_rad WHERE com_id IN(SELECT com_id FROM (SELECT com_id, cm.is_num (r_val) chkk FROM   importer.gen_rad)WHERE   chkk = 0)AND cm.tonumeric (r_val) "
						// + startCond1
						// + startValue1
						// +
						// "  and com_id in (select com_id from cm.part_codewhere pl_id="
						// + pl.getPlId()
						// + " and clas_id ="
						// + classID
						// + " and manual_flag = 0))")
						// .executeUpdate();
						// if (!session.getTransaction().isInitiator())
						// session.beginTransaction();
						// session.getTransaction().commit();
						//
						// session.createSQLQuery(
						// "insert into cm.part_code(COM_ID,CLAS_ID,DYN_CLASS,REF_URL,round,manual_flag,MODIFY_DATE,PL_id) select com_id,"
						// + classID
						// + ",'"
						// + code
						// + "','"
						// + refURL
						// + "',2,0,sysdate,"
						// + pl.getPlId()
						// +
						// " from importer.gen_rad where com_id in (select com_id from(select com_id ,cm.is_num(r_val) chkk from importer.gen_rad )where chkk=0) and cm.tonumeric (r_val) "
						// + startCond1
						// + startValue1
						// + " and cm.tonumeric(r_val)"
						// + endCond1
						// + endVal1
						// +
						// " minus  (select com_id from cm.part_codewhere clas_id ="
						// + classID
						// + " and round = 2  and pl_id ="
						// + pl.getPlId() + ")")
						// .executeUpdate();
						// if (!session.getTransaction().isInitiator())
						// session.beginTransaction();
						// session.getTransaction().commit();
						//
						// }
						//
						// } else if (!endCond1.equals("")) {
						// String que = "update cm.part_codeset pl_id= "
						// + pl.getPlId()
						// + " ,DYN_CLASS='"
						// + code
						// + "',REF_URL='"
						// + refURL
						// +
						// "',round=2,ref='EXCEPTION',SE_CLASS = STATIC_CLASS, MODIFY_DATE=sysdate where clas_id="
						// + classID
						// +
						// " and com_id in(SELECT com_id FROM  importer.gen_rad WHERE com_id IN(SELECT com_id FROM (SELECT com_id, cm.is_num (r_val) chkk FROM   importer.gen_rad )WHERE   chkk = 0)AND cm.tonumeric (r_val) "
						// + startCond1
						// + startValue1
						// + " AND cm.tonumeric (r_val) "
						// + endCond1
						// + endVal1
						// +
						// " and com_id in (select com_id from cm.part_codewhere pl_id="
						// + pl.getPlId()
						// + " and clas_id ="
						// + classID
						// + " and manual_flag = 1 and (ROUND  is null ) ))";
						// System.out.println(que);
						// session.createSQLQuery(que).executeUpdate();
						// if (!session.getTransaction().isInitiator())
						// session.beginTransaction();
						// session.getTransaction().commit();
						//
						// session.createSQLQuery(
						// "update cm.part_codeset pl_id= "
						// + pl.getPlId()
						// + " ,DYN_CLASS='"
						// + code
						// + "',REF_URL='"
						// + refURL
						// +
						// "',manual_flag=0,round=2, MODIFY_DATE=sysdate where clas_id="
						// + classID
						// +
						// " and com_id in(SELECT com_id FROM  importer.gen_rad WHERE com_id IN(SELECT com_id FROM (SELECT com_id, cm.is_num (r_val) chkk FROM   importer.gen_rad )WHERE   chkk = 0)AND cm.tonumeric (r_val) "
						// + startCond1
						// + startValue1
						// + " AND cm.tonumeric (r_val) "
						// + endCond1
						// + endVal1
						// +
						// " and com_id in (select com_id from cm.part_codewhere pl_id="
						// + pl.getPlId()
						// + " and clas_id ="
						// + classID
						// + " and manual_flag = 0 and (ROUND  is null ) ))")
						// .executeUpdate();
						//
						// session.createSQLQuery(
						// "insert into cm.part_code(COM_ID,CLAS_ID,DYN_CLASS,REF_URL,round,manual_flag,MODIFY_DATE,PL_id,round) select com_id,"
						// + classID
						// + ",'"
						// + code
						// + "','"
						// + refURL
						// + "',2,0,sysdate,"
						// + pl.getPlId()
						// +
						// " ,2  from importer.gen_rad where com_id in (select com_id from(select com_id ,cm.is_num(r_val) chkk from importer.gen_rad)where chkk=0) and cm.tonumeric (r_val) "
						// + startCond1
						// + startValue1
						// + " and cm.tonumeric(r_val) "
						// + endCond1
						// + endVal1
						// +
						// "  minus  (select com_id from cm.part_codewhere clas_id ="
						// + classID
						// + " and  round = 2  and pl_id ="
						// + pl.getPlId() + ")")
						// .executeUpdate();
						// if (!session.getTransaction().isInitiator())
						// session.beginTransaction();
						// session.getTransaction().commit();
						//
						// } else {
						// String que = "update cm.part_codeset pl_id= "
						// + pl.getPlId()
						// + " ,DYN_CLASS='"
						// + code
						// + "',REF_URL='"
						// + refURL
						// +
						// "',round=2,ref='EXCEPTION',SE_CLASS=STATIC_CLASS, MODIFY_DATE=sysdate where clas_id="
						// + classID
						// +
						// " and com_id in(SELECT com_id FROM  importer.gen_rad WHERE com_id IN(SELECT com_id FROM (SELECT com_id, cm.is_num (r_val) chkk FROM   importer.gen_rad )WHERE   chkk = 0)AND cm.tonumeric (r_val) "
						// + startCond1
						// + startValue1
						// +
						// "  and com_id in (select com_id from cm.part_codewhere pl_id="
						// + pl.getPlId()
						// + " and clas_id ="
						// + classID + " and manual_flag = 1))";
						// session.createSQLQuery(que).executeUpdate();
						// if (!session.getTransaction().isInitiator())
						// session.beginTransaction();
						// session.getTransaction().commit();
						// session.createSQLQuery(
						// "update cm.part_codeset pl_id= "
						// + pl.getPlId()
						// + " ,DYN_CLASS='"
						// + code
						// + "',REF_URL='"
						// + refURL
						// + "',round=2, MODIFY_DATE=sysdate where clas_id="
						// + classID
						// +
						// " and com_id in(SELECT com_id FROM  importer.gen_rad WHERE com_id IN(SELECT com_id FROM (SELECT com_id, cm.is_num (r_val) chkk FROM   importer.gen_rad )WHERE   chkk = 0)AND cm.tonumeric (r_val) "
						// + startCond1
						// + startValue1
						// +
						// "  and com_id in (select com_id from cm.part_codewhere pl_id="
						// + pl.getPlId()
						// + " and clas_id =" + classID
						// + " and manual_flag = 0))")
						// .executeUpdate();
						// if (!session.getTransaction().isInitiator())
						// session.beginTransaction();
						// session.getTransaction().commit();
						// session.createSQLQuery(
						// "insert into cm.part_code(COM_ID,CLAS_ID,DYN_CLASS,REF_URL,round,manual_flag,MODIFY_DATE,PL_id,round) select com_id,"
						// + classID
						// + ",'"
						// + code
						// + "','"
						// + refURL
						// + "',0,sysdate,"
						// + pl.getPlId()
						// +
						// " ,2 from importer.gen_rad where com_id in (select com_id from(select com_id ,cm.is_num(r_val) chkk from importer.gen_rad)where chkk=0) and cm.tonumeric (r_val) "
						// + startCond1
						// + startValue1
						// +
						// "   minus  (select com_id from cm.part_codewhere clas_id ="
						// + classID
						// + " and round = 2  and pl_id ="
						// + pl.getPlId() + ")")
						// .executeUpdate();
						// if (!session.getTransaction().isInitiator())
						// session.beginTransaction();
						// session.getTransaction().commit();
						//
						// }
						//
						// }

					}

				}

				if (manName == null) {

					session.createSQLQuery(
							"insert into importer.part_code_temp (ip,COM_ID,PL_ID,CLAS_ID,DYN_CLASS,MODIFY_DATE,manual_flag,round) select '"
									+ ip
									+ "',com_id,cm.get_pl_id('"
									+ plName
									+ "'),"
									+ classID
									+ ",'',sysdate,0,5 from (select com_id from cm.dynamic_flat where pl_id=cm.get_pl_id('"
									+ plName
									+ "') minus (select com_id from importer.part_code_temp where  ip='"
									+ ip
									+ "'  and pl_id =cm.get_pl_id('"
									+ plName
									+ "') and CLAS_ID= "
									+ classID
									+ "))").executeUpdate();

					if (!session.getTransaction().isInitiator())
						session.beginTransaction();
					session.getTransaction().commit();

					session.createSQLQuery(
							"insert into importer.part_code_temp (ip,COM_ID,PL_ID,CLAS_ID,DYN_CLASS,MODIFY_DATE,manual_flag,ref_url,round) select '"
									+ ip
									+ "', com_id,cm.get_pl_id('"
									+ plName
									+ "'),"
									+ classID
									+ ",'"
									+ fast_date_code
									+ "',sysdate,0,'"
									+ fast_ref
									+ "' ,2 from (select com_id from cm.xlp_se_component  where pl_id=cm.get_pl_id('"
									+ plName
									+ "') minus (select com_id from importer.part_code_temp where pl_id =cm.get_pl_id('"
									+ plName
									+ "') and ip='"
									+ ip
									+ "'  and CLAS_ID=" + classID + "))")
							.executeUpdate();

					if (!session.getTransaction().isInitiator())
						session.beginTransaction();
					session.getTransaction().commit();
				} else {

					session.createSQLQuery(
							"insert into importer.part_code_temp (ip,COM_ID,PL_ID,CLAS_ID,DYN_CLASS,MODIFY_DATE,manual_flag,ref_url,round) select '"
									+ ip
									+ "',com_id,cm.get_pl_id('"
									+ plName
									+ "'),"
									+ classID
									+ ",'',sysdate,0,'"
									+ fast_ref
									+ "' ,2 from (select com_id from cm.dynamic_flat where man_id=cm.get_man_id('"
									+ manName
									+ "') and pl_id=cm.get_pl_id('"
									+ plName
									+ "') minus (select com_id from importer.part_code_temp where pl_id =cm.get_pl_id('"
									+ plName
									+ "') and ip='"
									+ ip
									+ "' and CLAS_ID=" + classID + "))")
							.executeUpdate();

					if (!session.getTransaction().isInitiator())
						session.beginTransaction();
					session.getTransaction().commit();

					session.createSQLQuery(
							"insert into importer.part_code_temp (ip,COM_ID,PL_ID,CLAS_ID,DYN_CLASS,MODIFY_DATE,manual_flag,round,ref_url) select '"
									+ ip
									+ "' com_id,cm.get_pl_id('"
									+ plName
									+ "'),"
									+ classID
									+ ",'"
									+ fast_date_code
									+ "',sysdate,0,'','"
									+ fast_ref
									+ "' from (select com_id from cm.xlp_se_component  where man_id=cm.get_man_id('"
									+ manName
									+ "') and pl_id=cm.get_pl_id('"
									+ plName
									+ "') minus (select com_id from importer.part_code_temp where pl_id =cm.get_pl_id('"
									+ plName
									+ "') and ip = '"
									+ ip
									+ "' and CLAS_ID=" + classID + "))")
							.executeUpdate();

					if (!session.getTransaction().isInitiator())
						session.beginTransaction();
					session.getTransaction().commit();
				}
			}
		}

	}

	public static void insertShValues(Set set, XlpSePl pl, String ip,
			Session session) {
		Iterator it = set.iterator();
		int cou = 0;
		int count = 0;

		while (it.hasNext()) {
			String fet_Name = it.next().toString();

			Criteria criteria = session
					.createCriteria(TblQchApprovedValues.class);
			criteria.add(Restrictions.eq("fetName", fet_Name));
			criteria.add(Restrictions.eq("xlpSePl", pl));
			List approved = criteria.list();

			criteria = session.createCriteria(XlpSeFeature.class);
			criteria.add(Restrictions.eq("fetName", fet_Name));
			criteria.add(Restrictions.eq("xlpSePl", pl));
			XlpSeFeature feature = (XlpSeFeature) criteria.uniqueResult();

			for (int j = 0; j < approved.size(); j++) {
				ShFeature obj = new ShFeature();
				obj.setIp(ip);
				TblQchApprovedValues app = (TblQchApprovedValues) approved
						.get(j);
				long fet_id = app.getXlpSeFeature().getFetId();
				BigDecimal val_id = app.getValueId();
				String value = app.getValue();
				String unit = app.getUnit();
				String sign = app.getSign();

				if ((sign != null) && (sign.equals("-"))) {
					value = "-" + value;
				}

				String colName = feature.getColNm();
				String multiplayer = app.getMultiplier();

				if (value.equals("N/A")) {

					obj.setColName(colName);
					obj.setFetId(new BigDecimal(fet_id));
					obj.setFetName(fet_Name);
					obj.setFetValue("N/A");
					obj.setValueId(val_id);

					obj.setUnit(unit);
					session.saveOrUpdate(obj);
					if (!session.getTransaction().isInitiator())
						session.beginTransaction();
					session.getTransaction().commit();

				} else {
					if (unit == null) {
						unit = "";
					}
					if (multiplayer == null) {
						multiplayer = "";
					}
					String[] mults = (String[]) null;
					String[] units = (String[]) null;
					String[] toValues = value.split("\\sto\\s");
					String[] toUnites = unit.split("\\sto\\s");
					String[] toMul = multiplayer.split("\\sto\\s");

					for (int k = 0; k < toValues.length; k++) {
						String[] values = toValues[k].split("!");
						if (!unit.equals("")) {
							units = toUnites[k].split("!");
						}

						if (!multiplayer.equals("")) {
							mults = toMul[k].split("!");
						}
						for (int s = 0; s < values.length; s++) {
							String string1 = "";
							String string2 = "";
							String string3 = "";
							String string = values[s].replaceAll("\\(", "");

							string1 = string.replaceAll("\\)", "");
							System.out.println("unit is " + unit);
							if (!unit.equals("")) {
								string = units[s].replaceAll("\\(", "");
								string2 = string.replaceAll("\\)", "");
							}
							System.out.println("multiplayer is " + multiplayer);
							if (!multiplayer.equals("")) {
								string = mults[s].replaceAll("\\(", "");
								string3 = string.replaceAll("\\)", "");
							}
							System.out.println("String1 is " + string1
									+ " and string2 is " + string2);
							String[] valueStic = string1.split("\\|");
							String[] unitStic = string2.split("\\|");
							String[] mulStic = string3.split("\\|");
							for (int m = 0; m < valueStic.length; m++) {
								if (valueStic[m].contains("/")) {
									String[] dataIssue = valueStic[m]
											.split("/");
									String past = dataIssue[0].replaceAll(
											"[\\d ]", "");
									String makam = dataIssue[1].replaceAll(
											"[\\d ]", "");
									if ((past.equals("")) && (makam.equals(""))) {
										if (dataIssue[0].contains(" ")) {
											String[] sa7e7 = dataIssue[0]
													.split(" ");
											valueStic[m] = ""
													+ (Double
															.parseDouble(sa7e7[0]) * (Double
															.parseDouble(sa7e7[1]) / Double
															.parseDouble(dataIssue[1])));
										} else {
											valueStic[m] = ""
													+ (Double
															.parseDouble(dataIssue[0]) / Double
															.parseDouble(dataIssue[1]));
										}
									}

								}

								double x = 0.0D;
								obj.setColName(colName);
								obj.setFetId(new BigDecimal(fet_id));
								obj.setFetName(fet_Name);

								obj.setValueId(val_id);
								try {
									obj.setUnit(unitStic[m]);
								} catch (Exception localException1) {
								}
								try {
									obj.setMultiplier(mulStic[m]);
									if (mulStic[m].equals("")) {
										if (valueStic[m].equals("9999"))
											obj.setFetValue("N/A");
										else
											obj.setFetValue(valueStic[m]);
									} else if (mulStic[m].equals("n")) {
										x = Double.parseDouble(valueStic[m]);
										x /= 1000000000.0D;
										System.out.println(x);
										obj.setFetValue("" + x);
									} else if (mulStic[m].equals("u")) {
										x = Double.parseDouble(valueStic[m]);
										x /= 1000000.0D;
										System.out.println("" + x);
										obj.setFetValue("" + x);
									} else if (mulStic[m].equals("m")) {
										x = Double.parseDouble(valueStic[m]);
										x /= 1000.0D;
										System.out.println(x);
										obj.setFetValue("" + x);
									} else if (mulStic[m].equals("G")) {
										x = Double.parseDouble(valueStic[m]);
										x *= 1000000000.0D;
										System.out.println("" + x);
										obj.setFetValue("" + x);
									} else if (mulStic[m].equals("M")) {
										x = Double.parseDouble(valueStic[m]);
										x *= 1000000.0D;
										System.out.println(x);
										obj.setFetValue("" + x);
									} else if ((mulStic[m].equals("K"))
											|| (mulStic[m].equals("k"))) {
										x = Double.parseDouble(valueStic[m]);
										x *= 1000.0D;
										System.out.println(x);
										obj.setFetValue("" + x);
									}
								} catch (Exception e) {
									if (valueStic[m].equals("9999"))
										obj.setFetValue("N/A");
									else {
										obj.setFetValue(valueStic[m]);
									}

								}

								session.saveOrUpdate(obj);
								if (!session.getTransaction().isInitiator())
									session.beginTransaction();
								session.getTransaction().commit();
								count++;
								if (count == 229) {
									System.out.println("here");
								}

							}
						}
					}
				}
			}
		}
	}

	public static String getWhereCondition(String featureN, String startCond,
			String rolUnit1, String rolUnit2, String endCond,
			String startValue, String endValue, String startMul, String endMul,
			XlpSePl pl, String ip, Session session) {
		String condition = "(";
		String sql = "select com_id ";
		String Equ = "";
		Equ = featureN.replace("E=", "");
		String where = " pl_id = " + pl.getPlId() + " ";

		Criteria c = session.createCriteria(XlpSeFeature.class);
		c.add(Restrictions.eq("fetName", featureN));
		c.add(Restrictions.eq("xlpSePl", pl));
		XlpSeFeature fet = (XlpSeFeature) c.list().get(0);

		String col = fet.getColNm();
		String values[] = null;
		condition += getValues(col, featureN, startCond, rolUnit1, rolUnit2,
				endCond, startValue, endValue, startMul, endMul, ip, session);

		condition += ")";

		return condition;
	}

	public static String getValues(String col, String featureN,
			String startCond, String rolUnit1, String rolUnit2, String endCond,
			String startValue, String endValue, String startMul, String endMul,
			String ip, Session session) {
		String[] values = (String[]) null;
		String condition = "";
		if (startValue.contains("|")) {
			values = startValue.split("\\|");
		} else {
			values = new String[1];
			values[0] = startValue;
		}
		for (int v = 0; v < values.length; v++) {
			String value = values[v];
			Double x = Double.valueOf(0.0D);
			if (startCond.equals("EQUAL")) {

				Criteria crit = session.createCriteria(ShFeature.class);
				crit.add(Restrictions.eq("fetValue", value));
				crit.add(Restrictions.eq("fetName", featureN));
				List list = crit.list();
				if (!list.isEmpty()) {
					for (int m = 0; m < list.size(); m++) {
						ShFeature shFet = (ShFeature) list.get(m);
						BigDecimal valID = shFet.getValueId();

						session.createSQLQuery(
								"insert into importer.VALUE_ID (VALUEID,ip)values ("
										+ valID + ",'" + ip + "')")
								.executeUpdate();
						if (!session.getTransaction().isInitiator())
							session.beginTransaction();
						session.getTransaction().commit();

					}

					condition = condition
							+ col
							+ " in (select VALUEID from importer.VALUE_ID where ip = '"
							+ ip + "')";
					System.out.println("condition is " + condition);
				} else {
					String str = "";
					for (int m = 0; m < condition.length() - 4; m++) {
						str += condition.charAt(m);
					}
					condition = str;

				}
			} else if (startCond.equals("CONTAIN")) {
				Criteria crit = session.createCriteria(ShFeature.class);
				crit.add(Restrictions.ilike("fetValue", "%" + value + "%"));
				crit.add(Restrictions.eq("fetName", featureN));
				List list = crit.list();

				if (!list.isEmpty()) {
					for (int m = 0; m < list.size(); m++) {
						ShFeature shFet = (ShFeature) list.get(m);
						BigDecimal valID = shFet.getValueId();

						session.createSQLQuery(
								"insert into importer.VALUE_ID (VALUEID,ip)values ("
										+ valID + ",'" + ip + "')")
								.executeUpdate();
						if (!session.getTransaction().isInitiator())
							session.beginTransaction();
						session.getTransaction().commit();
					}

					condition = condition
							+ col
							+ " in (select VALUEID from importer.VALUE_ID where ip = '"
							+ ip + "')";
					System.out.println("condition is " + condition);
				} else {
					condition = condition + "false";
				}
			} else if (startCond.equals("NOT CONTAIN")) {

				Criteria crit = session.createCriteria(ShFeature.class);
				crit.add(Restrictions.not(Restrictions.ilike("fetValue", "%"
						+ value + "%")));
				crit.add(Restrictions.eq("fetName", featureN));
				List list = crit.list();

				if (!list.isEmpty()) {
					for (int m = 0; m < list.size(); m++) {
						ShFeature shFet = (ShFeature) list.get(m);
						BigDecimal valID = shFet.getValueId();
						session.createSQLQuery(
								"insert into importer.VALUE_ID (VALUEID,ip)values ("
										+ valID + ",'" + ip + "')")
								.executeUpdate();
						if (!session.getTransaction().isInitiator())
							session.beginTransaction();
						session.getTransaction().commit();
					}

					condition = condition
							+ col
							+ " in (select VALUEID from importer.VALUE_ID where ip = '"
							+ ip + "')";

					System.out.println("condition is " + condition);
				} else {
					condition = condition + "false";
				}

			} else if (startCond.equals("NOT EQUAL")) {

				Criteria crit = session.createCriteria(ShFeature.class);
				crit.add(Restrictions.not(Restrictions.eq("fetValue", value)));
				crit.add(Restrictions.eq("fetName", featureN));
				List list = crit.list();
				if (!list.isEmpty()) {
					for (int m = 0; m < list.size(); m++) {
						ShFeature shFet = (ShFeature) list.get(m);
						BigDecimal valID = shFet.getValueId();
						session.createSQLQuery(
								"insert into importer.VALUE_ID (VALUEID,ip)values ("
										+ valID + ",'" + ip + "')")
								.executeUpdate();
						if (!session.getTransaction().isInitiator())
							session.beginTransaction();
						session.getTransaction().commit();
					}

					condition = condition
							+ col
							+ " in (select VALUEID from importer.VALUE_ID where ip = '"
							+ ip + "')";

					System.out.println("condition is " + condition);
				} else {
					condition = condition + "false";
				}
			} else if (startCond.equals("=")) {
				if (value.equals("")) {
					condition = condition + col + " is null";
				} else {
					if ((!startValue.equals("")) && (!startMul.equals(""))) {
						if (startMul.equals("n")) {
							x = Double.valueOf(Double.parseDouble(value));
							x = Double.valueOf(x.doubleValue() / 1000000000.0D);
							System.out.println(x);
							value = "" + x;
						} else if (startMul.equals("u")) {
							x = Double.valueOf(Double.parseDouble(value));
							x = Double.valueOf(x.doubleValue() / 1000000.0D);
							System.out.println(x);
							value = "" + x;
						} else if (startMul.equals("m")) {
							x = Double.valueOf(Double.parseDouble(value));
							x = Double.valueOf(x.doubleValue() / 1000.0D);
							System.out.println(x);
							value = "" + x;
						} else if (startMul.equals("G")) {
							x = Double.valueOf(Double.parseDouble(value));
							x = Double.valueOf(x.doubleValue() * 1000000000.0D);
							System.out.println(x);
							value = "" + x;
						} else if (startMul.equals("M")) {
							x = Double.valueOf(Double.parseDouble(value));
							x = Double.valueOf(x.doubleValue() * 1000000.0D);
							System.out.println(x);
							value = "" + x;
						} else if (startMul.equals("K") || startMul.equals("k")) {
							x = Double.valueOf(Double.parseDouble(value));
							x = Double.valueOf(x.doubleValue() * 1000.0D);
							System.out.println(x);
							value = "" + x;
						}
					}
					Criteria crit = session.createCriteria(ShFeature.class);

					crit.add(Restrictions.eq("fetValue", value));

					crit.add(Restrictions.eq("fetName", featureN));
					List list = crit.list();

					if (!list.isEmpty()) {
						for (int m = 0; m < list.size(); m++) {
							ShFeature shFet = (ShFeature) list.get(m);
							BigDecimal valID = shFet.getValueId();
							session.createSQLQuery(
									"insert into importer.VALUE_ID (VALUEID,ip)values ("
											+ valID + ",'" + ip + "')")
									.executeUpdate();
							if (!session.getTransaction().isInitiator())
								session.beginTransaction();
							session.getTransaction().commit();
						}

						condition = condition
								+ col
								+ " in (select VALUEID from importer.VALUE_ID where ip = '"
								+ ip + "')";

						System.out.println("condition is " + condition);
					} else {
						condition = condition + "false";
					}

				}

			} else if (endCond.equals("")) {
				if (!startMul.equals("")) {
					if (startMul.equals("n")) {
						x = Double.valueOf(Double.parseDouble(value));
						x = Double.valueOf(x.doubleValue() / 1000000000.0D);
						System.out.println(x);
						value = "" + x;
					} else if (startMul.equals("u")) {
						x = Double.valueOf(Double.parseDouble(value));
						x = Double.valueOf(x.doubleValue() / 1000000.0D);
						System.out.println(x);
						value = "" + x;
					} else if (startMul.equals("m")) {
						x = Double.valueOf(Double.parseDouble(value));
						x = Double.valueOf(x.doubleValue() / 1000.0D);
						System.out.println(x);
						value = "" + x;
					} else if (startMul.equals("G")) {
						x = Double.valueOf(Double.parseDouble(value));
						x = Double.valueOf(x.doubleValue() * 1000000000.0D);
						System.out.println(x);
						value = "" + x;
					} else if (startMul.equals("M")) {
						x = Double.valueOf(Double.parseDouble(value));
						x = Double.valueOf(x.doubleValue() * 1000000.0D);
						System.out.println(x);
						value = "" + x;
					} else if (startMul.equals("K") || startMul.equals("k")) {
						x = Double.valueOf(Double.parseDouble(value));
						x = Double.valueOf(x.doubleValue() * 1000.0D);
						System.out.println(x);
						value = "" + x;
					}
				}
				List list = null;

				if (rolUnit1.equals("")) {
					session.createSQLQuery(
							"insert into importer.value_id (VALUEID,ip) SELECT  distinct value_id,'"
									+ ip
									+ "' FROM (SELECT   value_id,unit,fet_name, chkk,fet_value, COL_NAME FROM (SELECT   value_id,unit, fet_name, COL_NAME,cm.is_num (fet_value) chkk,fet_value FROM   importer.sh_feature) WHERE chkk = 0) x WHERE   cm.tonumeric (fet_value) "
									+ startCond + value
									+ " and unit is null and FET_NAME='"
									+ featureN + "'").executeUpdate();
					if (!session.getTransaction().isInitiator())
						session.beginTransaction();
					session.getTransaction().commit();
				} else {
					String un1 = rolUnit1;
					session.createSQLQuery(
							"insert into importer.value_id (VALUEID,ip) SELECT  distinct value_id ,'"
									+ ip
									+ "' FROM (SELECT   value_id,unit,fet_name, chkk,fet_value, COL_NAME FROM (SELECT   value_id,unit, fet_name, COL_NAME,cm.is_num (fet_value) chkk,fet_value FROM   importer.sh_feature) WHERE chkk = 0) x WHERE   cm.tonumeric (fet_value) "
									+ startCond + value + " and unit = '" + un1
									+ "' and FET_NAME='" + featureN + "'")
							.executeUpdate();
					if (!session.getTransaction().isInitiator())
						session.beginTransaction();
					session.getTransaction().commit();
				}

				list = session.createSQLQuery(
						"select VALUEID from importer.VALUE_ID where ip = '"
								+ ip + "'").list();
				if (!list.isEmpty()) {
					condition = condition
							+ col
							+ " in (select VALUEID from importer.VALUE_ID where ip = '"
							+ ip + "')";

					System.out.println("condition is " + condition);
				} else {
					condition = condition + "false";
				}
			} else {
				String un1 = rolUnit1;
				if (!startMul.equals("")) {
					if (startMul.equals("n")) {
						x = Double.valueOf(Double.parseDouble(value));
						x = Double.valueOf(x.doubleValue() / 1000000000.0D);
						System.out.println(x);
						value = "" + x;
					} else if (startMul.equals("u")) {
						x = Double.valueOf(Double.parseDouble(value));
						x = Double.valueOf(x.doubleValue() / 1000000.0D);
						System.out.println(x);
						value = "" + x;
					} else if (startMul.equals("m")) {
						x = Double.valueOf(Double.parseDouble(value));
						x = Double.valueOf(x.doubleValue() / 1000.0D);
						System.out.println(x);
						value = "" + x;
					} else if (startMul.equals("G")) {
						x = Double.valueOf(Double.parseDouble(value));
						x = Double.valueOf(x.doubleValue() * 1000000000.0D);
						System.out.println(x);
						value = "" + x;
					} else if (startMul.equals("M")) {
						x = Double.valueOf(Double.parseDouble(value));
						x = Double.valueOf(x.doubleValue() * 1000000.0D);
						System.out.println(x);
						value = "" + x;
					} else if (startMul.equals("K") || startMul.equals("k")) {
						x = Double.valueOf(Double.parseDouble(value));
						x = Double.valueOf(x.doubleValue() * 1000.0D);
						System.out.println(x);
						value = "" + x;
					}
				}
				String value2 = endValue;
				String un2 = rolUnit2;

				if (!startMul.equals("")) {
					if (endMul.equals("n")) {
						x = Double.valueOf(Double.parseDouble(value2));
						x = Double.valueOf(x.doubleValue() / 1000000000.0D);

						System.out.println(x);
						value2 = "" + x;
					} else if (endMul.equals("u")) {
						x = Double.valueOf(Double.parseDouble(value2));
						x = Double.valueOf(x.doubleValue() / 1000000.0D);

						System.out.println(x);
						value2 = "" + x;
					} else if (endMul.equals("m")) {
						x = Double.valueOf(Double.parseDouble(value2));
						x = Double.valueOf(x.doubleValue() / 1000.0D);

						System.out.println(x);
						value2 = "" + x;
					} else if (endMul.equals("G")) {
						x = Double.valueOf(Double.parseDouble(value2));
						x = Double.valueOf(x.doubleValue() * 1000000000.0D);

						System.out.println(x);
						value2 = "" + x;
					} else if (endMul.equals("M")) {
						x = Double.valueOf(Double.parseDouble(value2));
						x = Double.valueOf(x.doubleValue() * 1000000.0D);

						System.out.println(x);
						value2 = "" + x;
					} else if (endMul.equals("K") || endMul.equals("k")) {
						x = Double.valueOf(Double.parseDouble(value2));
						x = Double.valueOf(x.doubleValue() * 1000.0D);

						System.out.println(x);
						value2 = "" + x;
					}
				}
				List list = null;
				if ((un1.equals("")) && (un2.equals(""))) {
					session.createSQLQuery(
							"insert into importer.value_id (VALUEID,ip) SELECT  distinct value_id,'"
									+ ip
									+ "' FROM (SELECT  value_id,unit,fet_name, chkk,fet_value, COL_NAME FROM (SELECT   value_id,unit, fet_name, COL_NAME,cm.is_num (fet_value) chkk,fet_value FROM   importer.sh_feature) WHERE chkk = 0) x WHERE   (cm.tonumeric (fet_value) "
									+ startCond
									+ value
									+ " and unit is null ) and(cm.tonumeric (fet_value) "
									+ endCond + value2
									+ " and unit is null) and FET_NAME ='"
									+ featureN + "'").executeUpdate();
					if (!session.getTransaction().isInitiator())
						session.beginTransaction();
					session.getTransaction().commit();
				} else if (un2.equals("")) {
					session.createSQLQuery(
							"insert into importer.value_id (VALUEID,ip) SELECT  distinct value_id ,'"
									+ ip
									+ "' FROM (SELECT  value_id,unit,fet_name, chkk,fet_value, COL_NAME FROM (SELECT   value_id,unit, fet_name, COL_NAME,cm.is_num (fet_value) chkk,fet_value FROM   importer.sh_feature) WHERE chkk = 0) x WHERE   (cm.tonumeric (fet_value) "
									+ startCond + value + " and unit = '" + un1
									+ "') and(cm.tonumeric (fet_value) "
									+ endCond + value2
									+ " and unit is null ) and FET_NAME ='"
									+ featureN + "'").executeUpdate();
					if (!session.getTransaction().isInitiator())
						session.beginTransaction();
					session.getTransaction().commit();
				} else if (un1.equals("")) {
					session.createSQLQuery(
							"insert into importer.value_id (VALUEID,ip) SELECT  distinct value_id,'"
									+ ip
									+ "' FROM (SELECT  value_id,unit,fet_name, chkk,fet_value, COL_NAME FROM (SELECT   value_id,unit, fet_name, COL_NAME,cm.is_num (fet_value) chkk,fet_value FROM   importer.sh_feature) WHERE chkk = 0) x WHERE   (cm.tonumeric (fet_value) "
									+ startCond
									+ value
									+ " and unit is null) and(cm.tonumeric (fet_value) "
									+ endCond + value2 + " and unit = '" + un2
									+ "' ) and FET_NAME ='" + featureN + "'")
							.executeUpdate();
					if (!session.getTransaction().isInitiator())
						session.beginTransaction();
					session.getTransaction().commit();
				} else {
					session.createSQLQuery(
							" insert into importer.value_id (VALUEID,ip) SELECT  distinct value_id,'"
									+ ip
									+ "' FROM (SELECT  value_id,unit,fet_name, chkk,fet_value, COL_NAME FROM (SELECT   value_id,unit, fet_name, COL_NAME,cm.is_num (fet_value) chkk,fet_value FROM   importer.sh_feature) WHERE chkk = 0) x WHERE   (cm.tonumeric (fet_value) "
									+ startCond + value + " and unit = '" + un1
									+ "') and(cm.tonumeric (fet_value) "
									+ endCond + value2 + " and unit = '" + un2
									+ "' ) and FET_NAME ='" + featureN + "'")
							.executeUpdate();
					if (!session.getTransaction().isInitiator())
						session.beginTransaction();
					session.getTransaction().commit();
				}

				List ids = session.createSQLQuery(
						"select valueid from importer.value_id where ip = '"
								+ ip + "'").list();

				if (!ids.isEmpty()) {
					condition = condition
							+ col
							+ " in (select VALUEID from importer.VALUE_ID where ip = '"
							+ ip + "')";

					System.out.println("condition is " + condition);
				} else {
					condition = condition + "false";
				}
			}

			if (v != values.length - 1) {
				condition = condition + " or ";
			}
		}

		if (condition.startsWith(" or")) {
			String str = "";
			for (int i = 3; i < condition.length(); i++) {
				str += condition.charAt(i);
			}
			condition = str;
		}
		return condition;
	}

	public static Set getFeatures(String[] arr) {
		Set features = new HashSet();
		for (int i = 0; i < arr.length; i++) {
			String str = arr[i].replaceAll("[\\d\\.]", "");
			if (!str.equals("")) {
				features.add(str);
			}
		}
		return features;
	}

	public void runTruthByMan(String manName, int classID) {
		try {
			Session session = DataBaseConnection.getSession();
			String sql = "call importer.GEN_TRUTH_by_man(cm.get_man_id('"
					+ manName + "')," + classID + ") ";
			System.out.println(sql);
			session.createSQLQuery(sql).executeUpdate();
			if (!session.getTransaction().isInitiator())
				session.beginTransaction();
			session.getTransaction().commit();
			System.out.println("here");

		} catch (Exception e) {
			System.out.println("error here");
			e.printStackTrace();
		}
	}

	public void generateRolesByPL(String plName, int classID) {

		try {
			Session session = DataBaseConnection.getSession();
			String sql = "call importer.GEN_TRUTH(cm.get_pl_id('" + plName
					+ "')," + classID + ") ";
			System.out.println(sql);
			session.createSQLQuery(sql).executeUpdate();
			if (!session.getTransaction().isInitiator())
				session.beginTransaction();
			session.getTransaction().commit();
			System.out.println("here");

		} catch (Exception e) {
			System.out.println("error here");
			e.printStackTrace();
		}

	}

	public void generateRolesByPN(String pn, String man, int classID) {
		System.out.println("here");
		try {
			Session session = DataBaseConnection.getSession();
			String sql = "call cm.GEN_TRUTH_com(cm.GET_COM_ID_BY_PN_MAN('" + pn
					+ "',cm.get_man_id('" + man
					+ "')),cm.GET_PL_ID_BY_PN_MAN('" + pn + "',cm.get_man_id('"
					+ man + "'))," + classID + ") ";
			System.out.println(sql);
			session.createSQLQuery(sql).executeUpdate();
			if (!session.getTransaction().isInitiator())
				session.beginTransaction();
			session.getTransaction().commit();
			System.out.println("here");

		} catch (Exception e) {
			System.out.println("error here");
			e.printStackTrace();
		}
	}
}
