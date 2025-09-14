/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.repositories.impl;

import com.pnh.pojo.RoomTypes;
import com.pnh.pojo.Rooms;
import com.pnh.pojo.ReservationRooms;
import com.pnh.pojo.Reservations;
import com.pnh.repositories.RoomRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Repository
@Transactional
public class RoomRepositoryImpl implements RoomRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    private static final int PAGE_SIZE = 6;

    @Override
    public List<Rooms> getRooms(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Rooms> query = b.createQuery(Rooms.class);
        Root root = query.from(Rooms.class);
        query.select(root);

        // Xử lý các bộ lọc nếu có
        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            // Tìm kiếm theo từ khóa (số phòng hoặc ghi chú)
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                Predicate kwPredicate = b.or(
                        b.like(root.get("roomNumber"), String.format("%%%s%%", kw)),
                        b.like(root.get("note"), String.format("%%%s%%", kw))
                );
                predicates.add(kwPredicate);
            }

            // Lọc theo loại phòng
            String typeId = params.get("roomTypeId");
            if (typeId != null && !typeId.isEmpty()) {
                predicates.add(b.equal(root.get("roomTypeId").get("id"), Long.valueOf(typeId)));
            }

            // Lọc theo trạng thái phòng
            String status = params.get("status");
            if (status != null && !status.isEmpty()) {
                predicates.add(b.equal(root.get("status"), status));
            }

            // Lọc theo tầng
            String floor = params.get("floor");
            if (floor != null && !floor.isEmpty()) {
                predicates.add(b.equal(root.get("floor"), Integer.valueOf(floor)));
            }

            // Tìm phòng trống theo ngày check-in/check-out
            String checkIn = params.get("checkIn");
            String checkOut = params.get("checkOut");
            if (checkIn != null && checkOut != null && !checkIn.isEmpty() && !checkOut.isEmpty()) {
                // Chỉ lấy phòng có sẵn (AVAILABLE, CLEANING)
                predicates.add(root.get("status").in("AVAILABLE", "CLEANING"));

                // Kiểm tra không trùng lịch với các đặt phòng khác
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<ReservationRooms> rr = subquery.from(ReservationRooms.class);
                Join<ReservationRooms, Reservations> res = rr.join("reservationId");

                subquery.select(b.literal(1L))
                        .where(b.and(
                                b.equal(rr.get("roomId").get("id"), root.get("id")),
                                res.get("status").in("HELD", "CONFIRMED", "CHECKED_IN"),
                                // OVERLAP condition: NOT( existing.checkOut <= desired.checkIn OR existing.checkIn >= desired.checkOut )
                                b.not(
                                        b.or(
                                                b.lessThanOrEqualTo(res.get("checkOut"), LocalDate.parse(checkIn)),
                                                b.greaterThanOrEqualTo(res.get("checkIn"), LocalDate.parse(checkOut))
                                        )
                                )
                        ));

                // Exclude rooms where any overlapping reservation exists
                predicates.add(b.not(b.exists(subquery)));
            }

            // Lọc theo sức chứa và giá của loại phòng
            Join<Rooms, RoomTypes> joinType = root.join("roomTypeId");
            String minCapacity = params.get("minCapacity");
            if (minCapacity != null && !minCapacity.isEmpty()) {
                predicates.add(b.greaterThanOrEqualTo(joinType.get("capacity"), Integer.valueOf(minCapacity)));
            }

            String maxPrice = params.get("maxPrice");
            if (maxPrice != null && !maxPrice.isEmpty()) {
                predicates.add(b.lessThanOrEqualTo(joinType.get("price"), new BigDecimal(maxPrice)));
            }

            // Áp dụng các điều kiện lọc
            if (!predicates.isEmpty()) {
                query.where(b.and(predicates.toArray(new Predicate[0])));
            }

            // Sắp xếp kết quả
            String orderBy = params.get("orderBy");
            if (orderBy != null && !orderBy.isEmpty()) {
                query.orderBy(b.asc(root.get(orderBy)));
            } else {
                query.orderBy(b.asc(root.get("roomNumber")));
            }
        }

        Query q = s.createQuery(query);

        // Phân trang
        if (params != null) {
            String p = params.get("page");
            if (p != null && !p.isEmpty()) {
                int page = Integer.parseInt(p);
                int start = (page - 1) * PAGE_SIZE;

                q.setMaxResults(PAGE_SIZE);
                q.setFirstResult(start);
            }
        }

        return q.getResultList();
    }

    @Override
    public Rooms getRoomById(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.find(Rooms.class, id);
    }

    @Override
    public void addOrUpdate(Rooms r) {
        Session s = this.factory.getObject().getCurrentSession();
        if (r.getId() == null) {
            s.persist(r);
        } else {
            s.merge(r);
        }
    }

    @Override
    public void deleteRoom(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        Rooms r = this.getRoomById(id);
        if (r != null) {
            s.remove(r);
        }
    }

    @Override
    public boolean existsByRoomNumber(String roomNumber) {
        Session s = this.factory.getObject().getCurrentSession();

        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Rooms> root = cq.from(Rooms.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("roomNumber"), roomNumber));

        Long count = s.createQuery(cq).getSingleResult();

        return count != null && count > 0;
    }

    @Override
    public int updateStatusByIds(List<Long> ids, String status) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaUpdate<Rooms> update = cb.createCriteriaUpdate(Rooms.class);

        Root<Rooms> root = update.from(Rooms.class);
        update.set(root.get("status"), status);
        update.where(root.get("id").in(ids));

        return s.createQuery(update).executeUpdate();
    }

    @Override
    public long countByStatus(String status) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Rooms> root = cq.from(Rooms.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("status"), status));

        Long count = s.createQuery(cq).getSingleResult();
        return count != null ? count : 0L;
    }

    @Override
    public long countByRoomType(Long roomTypeId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Rooms> root = cq.from(Rooms.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("roomTypeId").get("id"), roomTypeId));

        Long count = s.createQuery(cq).getSingleResult();
        return count != null ? count : 0L;
    }

}
