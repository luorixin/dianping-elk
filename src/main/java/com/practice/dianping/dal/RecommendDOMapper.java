package com.practice.dianping.dal;

import com.practice.dianping.model.RecommendDO;

public interface RecommendDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Sat Apr 11 17:03:32 CST 2020
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Sat Apr 11 17:03:32 CST 2020
     */
    int insert(RecommendDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Sat Apr 11 17:03:32 CST 2020
     */
    int insertSelective(RecommendDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Sat Apr 11 17:03:32 CST 2020
     */
    RecommendDO selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Sat Apr 11 17:03:32 CST 2020
     */
    int updateByPrimaryKeySelective(RecommendDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Sat Apr 11 17:03:32 CST 2020
     */
    int updateByPrimaryKey(RecommendDO record);
}