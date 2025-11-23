package net.swordie.ms.client.character.skills;

import javax.persistence.*;

/**
 * matrix增强插槽
 * @author Firiz
 * Created on 6/21/2023.
 */
@Entity
@Table(name = "matrix_enhance_slots")
public class MatrixEnhanceSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int level;
    private int position;

    public MatrixEnhanceSlot() {
    }

    public MatrixEnhanceSlot(int level, int position) {
        this.level = level;
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
