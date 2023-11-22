package com.example.springsecr.models;

import com.example.springsecr.services.DepartmentService;
import com.example.springsecr.services.RoleService;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.*;

@Entity
@Table(name = "department", indexes = {
        @Index(name = "idx_department_department_parent_id", columnList = "department_parent_id"),
        @Index(name = "idx_department_name", columnList = "name")
})
@Setter
@Getter
public class Department
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "department_generator_id_seq")
    @SequenceGenerator(name = "department_generator_id_seq", sequenceName = "department_generator_id_seq")
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    @Column(name = "name", unique = true)
    @NotBlank
    @Size(min = 5)
    private String name;
    @OneToOne(optional = true)
    @JoinTable(
            name = "department_user",
            joinColumns = {@JoinColumn(name = "department_id", unique = true, nullable = false),},
            inverseJoinColumns = {@JoinColumn(name = "user_id", unique = true, nullable = false)}
    )
    //@OnDelete(action = OnDeleteAction.SET_NULL)
    private User moderator;
    @OneToOne(optional = true)
    @JoinTable(
            name = "department_boss",
            joinColumns = {@JoinColumn(name = "department_id", unique = true,nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "boss_id", unique = true,nullable = false)}
    )
    //@OnDelete(action = OnDeleteAction.SET_NULL)
    private User boss;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_parent_id")
    private Department departmentParent;

    @OneToMany(mappedBy = "departmentParent")
    private Set<Department> departments = new HashSet<>();

    @OneToMany(mappedBy = "department")
    @Setter(AccessLevel.PRIVATE)
    private Set<User> employers = new HashSet<>();

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public Department() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Department that = (Department) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }


    @Override
    public int hashCode() {
        return name.hashCode();
    }


    public void setModerator(User moderator) {
        if (Objects.isNull(moderator))
        {
            if(Objects.nonNull(this.moderator))
            {
                this.moderator.setModeratorBy(null);
                // TODO: 21.11.2023 Изменить
            }
            this.moderator = null;
        }
        else if(!Objects.equals(moderator, this.moderator))
        {
            //Если новый модератор является модератором другого отдела - тогда отвязать его от модерирования прошлого отдела
            if(Objects.nonNull(moderator.getModeratorBy()))
            {
                moderator.getModeratorBy().setModerator(null);
            }
            //Убрать прошлого модератора текущего отдела
            setModerator(null);

            //Установка нового модератора в отдел
            this.moderator = moderator;
            this.moderator.setModeratorBy(this);
        }

        //moderator.setModeratorBy(this);
    }

    public void setBoss(User boss) {
        if(Objects.isNull(boss))
        {
            if(Objects.nonNull(this.boss))
            {
                this.boss.setBossBy(null);
                //this.boss.setDepartment(null);
                this.boss.setPosition("");
            }
            this.boss = null;
        }
        else if(!Objects.equals(boss, this.boss))
        {
            //Если новый босс является боссом другого отдела - то следует отвязать босса от прошлого отдела
            if(Objects.nonNull(boss.getBossBy()))
            {
                boss.getBossBy().setBoss(null);
            }

            //Отвязать текущего басса
            setBoss(null);

            //Установка нового босса
            this.boss = boss;
            this.boss.setBossBy(this);
            this.boss.setPosition(DepartmentService.BOSS_POSITION);


            if(!Objects.equals(this.boss.getDepartment(),this))
            {
                this.boss.setDepartment(this);
            }
        }
    }
}
