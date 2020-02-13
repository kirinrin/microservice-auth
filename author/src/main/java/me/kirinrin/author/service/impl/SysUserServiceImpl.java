package me.kirinrin.author.service.impl;

import me.kirinrin.author.entity.SysUser;
import me.kirinrin.author.mapper.SysUserMapper;
import me.kirinrin.author.service.SysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author dax
 */
@Service
public class SysUserServiceImpl implements SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser queryByUsername(String username) {
        return sysUserMapper.queryByUsername(username);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer addUser(SysUser sysUser) {
        return sysUserMapper.addUser(sysUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateUser(SysUser sysUser) {
        return sysUserMapper.updateUser(sysUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer removeUser(SysUser sysUser) {
        return sysUserMapper.removeUser(sysUser);
    }
}
