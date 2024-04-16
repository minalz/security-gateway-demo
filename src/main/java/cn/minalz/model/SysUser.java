package cn.minalz.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SysUser {
   
    /** 用户序号 */
    private Long userId;

    /** 部门编号 */
    private Long deptId;

    /** 登录名称 */
    private String userName;

    /** 用户名称 */
    private String nickName;

    /** 用户邮箱 */
    private String email;
    
    /** 手机号码 */
    private String phonenumber;

    /** 用户性别", readConverterExp = "0=男,1=女,2=未知 */
    private String sex;

    /** 用户头像 */
    private String avatar;

    /** 密码 */
    private String password;

    /** 帐号状态", readConverterExp = "0=正常,1=停用 */
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录时间 */
    private LocalDate loginDate;

    /** 角色对象 */
    private List<SysRole> roles;

    /** 角色组 */
    private Long[] roleIds;

    /** 岗位组 */
    private Long[] postIds;
    
}