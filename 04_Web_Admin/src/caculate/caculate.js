/**
 * 过滤搜索条件
 */
export const filterSearch = searchID => {
    if (!searchID || (!String(searchID).length)) {
        return false;
    } else {

        if (isNaN(searchID * 1) || searchID * 1 < 0 ||
            (searchID.toString().split('.')[1] !== undefined)) {

            return false;

        } else {
            return true;
        }
    }
}