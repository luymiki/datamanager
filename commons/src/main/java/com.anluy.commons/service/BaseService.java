/*
 * Copyright 2017 com.anluy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anluy.commons.service;


import com.anluy.commons.BaseEntity;
import com.anluy.commons.dao.BaseDAO;

import java.util.List;
import java.util.UUID;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2017/8/6.
 */
public abstract class BaseService<PK,T extends BaseEntity<PK>> {

    public abstract BaseDAO getBaseDAO();

    /**
     * 保存一条数据
     * @param entity
     */
    public T save(T entity) {
        if (entity.getId() == null) {
            String uuid = UUID.randomUUID().toString();
            entity.setId((PK)uuid);
        }
        getBaseDAO().save(entity);
        return entity;
    }


    /**
     * 根据主键查询
     * @param id
     * @return
     */
    public T get(PK id){
        return (T) getBaseDAO().get(id);
    }

    /**
     * 查询所有的数据
     * @return
     */
    public List<T> getAll(){
        return getBaseDAO().getAll();
    }

    /**
     * 更新一条数据
     * @param entity
     */
    public T update(T entity){
        return (T) getBaseDAO().update(entity);
    }

    /**
     * 删除一条记录
     * @param id
     * @return
     */
    public int delete(PK id){
        return  getBaseDAO().delete(id);
    }

}
