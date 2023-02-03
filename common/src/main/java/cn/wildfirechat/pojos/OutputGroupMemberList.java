/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.pojos;

import java.util.List;

public class OutputGroupMemberList {
    private List<PojoGroupMember> members;
    private int total;

    public List<PojoGroupMember> getMembers() {
        return members;
    }

    public void setMembers(List<PojoGroupMember> members) {
        this.members = members;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
