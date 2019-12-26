package hua.chuang.service.impl;

import hua.chuang.enums.Sex;
import hua.chuang.mapper.StuMapper;
import hua.chuang.mapper.UsersMapper;
import hua.chuang.pojo.Stu;
import hua.chuang.pojo.Users;
import hua.chuang.pojo.bo.UserBO;
import hua.chuang.service.StuService;
import hua.chuang.service.UserService;
import hua.chuang.utils.DateUtil;
import hua.chuang.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

@Service
public class UsersServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    private static final String USER_FACE = "http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAIlFXAAAcIhVPdSg994.png";


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {

        Example example = new Example(Users.class);
        Example.Criteria userCriteria = example.createCriteria();

        userCriteria.andEqualTo("username",username);

        Users users = usersMapper.selectOneByExample(example);
        return users!=null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(UserBO userBO) {

        String userId = sid.nextShort();

        Users users = new Users();
        users.setId(userId);
        users.setUsername(userBO.getUsername());
        try {
            users.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //默认昵称同用户名
        users.setNickname(userBO.getUsername());
        //默认头像
        users.setFace(USER_FACE);
        //默认生日
        users.setBirthday(DateUtil.stringToDate("1900-01-01"));
        //默认性别 保密
        users.setSex(Sex.secret.type);

        users.setUpdatedTime(new Date());
        users.setCreatedTime(new Date());

        usersMapper.insert(users);

        return users;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example example = new Example(Users.class);
        Example.Criteria userCriteria = example.createCriteria();

        userCriteria.andEqualTo("username",username);
        userCriteria.andEqualTo("password",password);

        Users result = usersMapper.selectOneByExample(example);

        return result;
    }


}
